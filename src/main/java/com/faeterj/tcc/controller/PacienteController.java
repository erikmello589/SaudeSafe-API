package com.faeterj.tcc.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreatePacienteDTO;
import com.faeterj.tcc.dto.RequestResponseDTO;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.service.PacienteService;
import com.faeterj.tcc.service.UserService;

import java.util.UUID;

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

    private final PacienteService pacienteService;
    private final UserService userService;

    public PacienteController(PacienteService pacienteService, UserService userService) {
        this.pacienteService = pacienteService;
        this.userService = userService;
    }

    @PostMapping("/paciente")
    public ResponseEntity<RequestResponseDTO> criarPaciente(@RequestBody CreatePacienteDTO dto, JwtAuthenticationToken token) {
        try 
        {  
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            pacienteService.criarPaciente(dto, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Paciente criado com sucesso.", 201));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }
    
    @DeleteMapping("/paciente/{id}")
    public ResponseEntity<RequestResponseDTO> deletaPaciente (@PathVariable("id") Long idPaciente, JwtAuthenticationToken token)
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            pacienteService.deletarPaciente(idPaciente, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Paciente Deletado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @GetMapping("/pacientes")
    public ResponseEntity<?> listaPacientesUser(@RequestParam(value = "page", defaultValue = "0") int page, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            var listaPacientes = pacienteService.listarPacientes(page, pageSize, user);
            return ResponseEntity.status(HttpStatus.OK).body(listaPacientes);
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }
}
