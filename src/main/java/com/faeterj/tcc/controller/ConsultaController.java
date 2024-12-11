package com.faeterj.tcc.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateConsultaDTO;
import com.faeterj.tcc.dto.RequestResponseDTO;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.service.ConsultaService;
import com.faeterj.tcc.service.UserService;

@RestController
public class ConsultaController {

    private final ConsultaService consultaService;
    private final UserService userService;

    public ConsultaController(ConsultaService consultaService, UserService userService) {
        this.consultaService = consultaService;
        this.userService = userService;
    }

    @PostMapping("/Consulta")
    public ResponseEntity<RequestResponseDTO> criarConsulta(@RequestBody CreateConsultaDTO dto, JwtAuthenticationToken token) 
    {
        try 
        {
            User user = userService.acharUserPorId(UUID.fromString(token.getName()));
            consultaService.criarConsulta(dto, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Consulta criada com sucesso.", 201));
        } 
        catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RequestResponseDTO(e.getReason(), 404));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getReason(), 500));
        }
    }
}
