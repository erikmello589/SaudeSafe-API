package com.faeterj.tcc.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreatePacienteDTO;
import com.faeterj.tcc.dto.RequestResponseDTO;

import com.faeterj.tcc.service.PacienteService;

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

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping("/NovoPaciente")
    public ResponseEntity<RequestResponseDTO> criarPaciente(@RequestBody CreatePacienteDTO dto, JwtAuthenticationToken token) {
        try 
        {
            pacienteService.criarPaciente(dto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Paciente criado com sucesso.", 201));
        } 
        catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestResponseDTO(e.getReason(), 404));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getReason(), 500));
        }
    }
    
    @DeleteMapping("/DeletePaciente/{id}")
    public ResponseEntity<RequestResponseDTO> deletaPaciente (@PathVariable("id") Long idPaciente, JwtAuthenticationToken token)
    {
        try 
        {
            pacienteService.deletarPaciente(idPaciente, token);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Paciente Deletado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) 
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestResponseDTO(e.getReason(), 404));
            }
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) 
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RequestResponseDTO(e.getReason(), 403));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getReason(), 500));
        }
    }

    @GetMapping("/Pacientes")
    public ResponseEntity<?> listaPacientesUser(@RequestParam(value = "page", defaultValue = "0") int page, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            JwtAuthenticationToken token) 
    {
        try 
        {
            var listaPacientes = pacienteService.listarPacientes(page, pageSize, token);
            return ResponseEntity.status(HttpStatus.OK).body(listaPacientes);
        } 
        catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestResponseDTO(e.getReason(), 404));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getReason(), 500));
        }
    }

    
    
}
