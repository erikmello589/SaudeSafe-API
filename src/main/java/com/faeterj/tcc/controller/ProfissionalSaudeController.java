package com.faeterj.tcc.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.RequestResponseDTO;
import com.faeterj.tcc.service.ProfissionalSaudeService;

@RestController
public class ProfissionalSaudeController {

    private final ProfissionalSaudeService profissionalSaudeService;

    public ProfissionalSaudeController(ProfissionalSaudeService profissionalSaudeService) {
        this.profissionalSaudeService = profissionalSaudeService;
    }

    @GetMapping("/Profissionais")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> listaProfissionais(@RequestParam(value = "page", defaultValue = "0") int page, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            JwtAuthenticationToken token) 
    {
        try 
        {
            var listaProfissionais = profissionalSaudeService.listarProfissionais(page, pageSize, token);
            return ResponseEntity.status(HttpStatus.OK).body(listaProfissionais);
        } 
        catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestResponseDTO(e.getReason(), 404));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getReason(), 500));
        }
    }

    @GetMapping("/VerificarProfissional/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<RequestResponseDTO> verificaProfissional(@PathVariable("id") UUID idProfissional, JwtAuthenticationToken token) 
    {
        try 
        {
            profissionalSaudeService.verificarProfissional(idProfissional, token);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Profissional verificado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestResponseDTO(e.getReason(), 404));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getReason(), 500));
        }
    }
}
