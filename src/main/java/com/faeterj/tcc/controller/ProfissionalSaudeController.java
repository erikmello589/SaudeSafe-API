package com.faeterj.tcc.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateProfissionalDTO;
import com.faeterj.tcc.dto.EditaProfissionalDTO;
import com.faeterj.tcc.dto.RequestResponseDTO;
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

    @GetMapping("/profissionais")
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
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @PostMapping("/profissional")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<RequestResponseDTO> criarProfissional(@RequestBody CreateProfissionalDTO dto, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            profissionalSaudeService.criarProfissional(dto, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Profissional criado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @PutMapping("/profissional/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<RequestResponseDTO> editaProfissional(@PathVariable("id") Long idProfissional, @RequestBody EditaProfissionalDTO dto, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            profissionalSaudeService.editarProfissional(idProfissional, user, dto);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Profissional Editado com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @DeleteMapping("/profissional/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<RequestResponseDTO> deletaProfissional(@PathVariable("id") Long idProfissional, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            profissionalSaudeService.deletarProfissional(idProfissional, user);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Profissional excluido com sucesso.", 200));
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @GetMapping("/profissional")
    public ResponseEntity<?> buscaProfissionalPorNumeroConselho(@RequestParam String numeroClasseConselho, @RequestParam String estado) 
    {
        try 
        {
            var profissional = profissionalSaudeService.buscarProfissionalDTOPorConselhoEstado(numeroClasseConselho, estado);
            return ResponseEntity.status(HttpStatus.OK).body(profissional);
        } 
        catch (ResponseStatusException e) 
        {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

}
