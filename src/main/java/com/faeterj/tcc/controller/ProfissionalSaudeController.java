package com.faeterj.tcc.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.RequestResponseDTO;
import com.faeterj.tcc.dto.VerificaProfissionalDTO;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.service.ProfissionalSaudeService;
import com.faeterj.tcc.service.UserService;

@RestController
public class ProfissionalSaudeController {

    private final UserService userService;
    private final ProfissionalSaudeService profissionalSaudeService;

    public ProfissionalSaudeController(UserService userService, ProfissionalSaudeService profissionalSaudeService) {
        this.userService = userService;
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
            userService.acharUserPorId(UUID.fromString(token.getName()));
            var listaProfissionais = profissionalSaudeService.listarProfissionais(page, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(listaProfissionais);
        } 
        catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestResponseDTO(e.getReason(), 404));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getReason(), 500));
        }
    }

    @PostMapping("/VerificarProfissional")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<RequestResponseDTO> verificaProfissional(@RequestBody VerificaProfissionalDTO dto, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            profissionalSaudeService.verificarProfissional(user, dto);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Profissional verificado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }
}
