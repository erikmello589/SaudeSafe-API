package com.faeterj.tcc.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreatePacienteDTO;
import com.faeterj.tcc.dto.ListaItemDTO;
import com.faeterj.tcc.dto.ListaPacientesDTO;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.repository.PacienteRepository;
import com.faeterj.tcc.repository.UserRepository;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class PacienteController {

    private final PacienteRepository pacienteRepository;

    private final UserRepository userRepository;

    public PacienteController(PacienteRepository pacienteRepository, UserRepository userRepository) {
        this.pacienteRepository = pacienteRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/NovoPaciente")
    public ResponseEntity<Void> criarPaciente(@RequestBody CreatePacienteDTO dto, JwtAuthenticationToken token) {
        
        var user = userRepository.findById(UUID.fromString(token.getName()));

        var paciente = new Paciente();
        paciente.setUser(user.get());
        paciente.setNomePaciente(dto.nomePaciente());
        paciente.setSobrenomePaciente(dto.sobrenomePaciente());

        pacienteRepository.save(paciente);

        return ResponseEntity.ok().build(); // Lembre-se de retornar o Paciente que foi criado

    }
    
    @DeleteMapping("/DeletePaciente/{id}")
    public ResponseEntity<Void> deletaPaciente (@PathVariable("id") Long idPaciente, JwtAuthenticationToken token)
    {
        var paciente = pacienteRepository.findById(idPaciente)
                                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var user = userRepository.findById(UUID.fromString(token.getName()));
        var isAdmin = user.get().getRoles()
                                .stream()
                                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || paciente.getUser().getUserID().equals(UUID.fromString(token.getName())))
        {
            pacienteRepository.deleteById(idPaciente);
        }
        else
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/Pacientes")
    public ResponseEntity<ListaPacientesDTO> listaPacientesUser(@RequestParam(value = "page", defaultValue = "0") int page, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            JwtAuthenticationToken token) 
    {
        // Verifica se o usuário autenticado existe
        var user = userRepository.findById(UUID.fromString(token.getName()))
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // Busca os pacientes associados ao User ID com paginação e ordenação
        var listaPacientesPage = pacienteRepository.findByUserUserID(user.getUserID(), PageRequest.of(page, pageSize, Sort.Direction.ASC, "dataCriacao"))
                                                .map(listaItem -> new ListaItemDTO(
                                                        listaItem.getPacienteId(), 
                                                        listaItem.getNomePaciente(), 
                                                        listaItem.getSobrenomePaciente(), 
                                                        listaItem.getDataCriacao()
                                                    ));

        // Retorna a resposta com os dados paginados
        return ResponseEntity.ok(new ListaPacientesDTO(
            listaPacientesPage.getContent(), 
            page, 
            pageSize, 
            listaPacientesPage.getTotalPages(), 
            listaPacientesPage.getTotalElements()
        ));
    }

    
    
}
