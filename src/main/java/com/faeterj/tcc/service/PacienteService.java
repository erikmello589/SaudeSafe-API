package com.faeterj.tcc.service;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreatePacienteDTO;
import com.faeterj.tcc.dto.ListaPacientesDTO;
import com.faeterj.tcc.dto.ReturnPacienteDTO;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.repository.PacienteRepository;
import com.faeterj.tcc.repository.UserRepository;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UserRepository userRepository;

    public PacienteService(PacienteRepository pacienteRepository, UserRepository userRepository) {
        this.pacienteRepository = pacienteRepository;
        this.userRepository = userRepository;
    }

    public void criarPaciente(CreatePacienteDTO dto, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Paciente paciente = new Paciente();
        paciente.setUser(user);
        paciente.setNomePaciente(dto.nomePaciente());
        paciente.setSobrenomePaciente(dto.sobrenomePaciente());

        pacienteRepository.save(paciente);
    }

    public void deletarPaciente(Long idPaciente, JwtAuthenticationToken token) {
        var paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));

        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || paciente.getUser().getUserID().equals(user.getUserID())) {
            pacienteRepository.deleteById(idPaciente);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autorizado a deletar este paciente");
        }
    }

    public ListaPacientesDTO listarPacientes(int page, int pageSize, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

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
}
