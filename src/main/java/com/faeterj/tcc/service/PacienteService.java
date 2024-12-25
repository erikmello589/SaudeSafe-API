package com.faeterj.tcc.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreatePacienteDTO;
import com.faeterj.tcc.dto.ListaPacientesDTO;
import com.faeterj.tcc.dto.ReturnPacienteDTO;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public void criarPaciente(CreatePacienteDTO dto, User user) 
    {
        Paciente paciente = new Paciente();
        paciente.setUser(user);
        paciente.setNomePaciente(dto.nomePaciente());
        paciente.setSobrenomePaciente(dto.sobrenomePaciente());

        pacienteRepository.save(paciente);
    }

    public void deletarPaciente(Long idPaciente, User user) 
    {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || paciente.getUser().getUserID().equals(user.getUserID())) {
            pacienteRepository.deleteById(idPaciente);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este paciente");
        }
    }

    public void editarPaciente(Long idPaciente, User user, CreatePacienteDTO dto) 
    {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || paciente.getUser().getUserID().equals(user.getUserID())) 
        {
            paciente.setNomePaciente(dto.nomePaciente());
            paciente.setSobrenomePaciente(dto.sobrenomePaciente());
            pacienteRepository.save(paciente);
        } 
        else 
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este paciente");
        }
    }

    public ListaPacientesDTO listarPacientes(int page, int pageSize, User user) 
    {
        var listaPacientesPage = pacienteRepository.findByUserUserID(user.getUserID(),
                PageRequest.of(page, pageSize, Sort.Direction.ASC, "dataCriacao"))
                .map(listaItem -> new ReturnPacienteDTO(
                        listaItem.getPacienteId(),
                        listaItem.getNomePaciente(),
                        listaItem.getSobrenomePaciente(),
                        listaItem.getDataCriacao()));

        return new ListaPacientesDTO(
                listaPacientesPage.getContent(),
                page,
                pageSize,
                listaPacientesPage.getTotalPages(),
                listaPacientesPage.getTotalElements());
    }

    public Paciente acharPacientePorId(Long idPaciente) {
        return pacienteRepository.findById(idPaciente)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));
    }
}
