package com.faeterj.tcc.controller;

import com.faeterj.tcc.dto.CreateUserDTO;
import com.faeterj.tcc.dto.EsqueciMinhaSenhaDTO;
import com.faeterj.tcc.dto.RedefinicaoSenhaDTO;
import com.faeterj.tcc.dto.RequestResponseDTO;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.service.PasswordResetService;
import com.faeterj.tcc.service.RedefinicaoSenhaService;
import com.faeterj.tcc.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Tag(name = "Usuário", description = "Endpoints para gerenciamento de Usuários e suas informações.")
public class UserController {

    private final UserService userService;

    private final PasswordResetService resetService;

    private final RedefinicaoSenhaService redefinicaoSenhaService;

    public UserController(UserService userService, PasswordResetService resetService,
            RedefinicaoSenhaService redefinicaoSenhaService) {
        this.userService = userService;
        this.resetService = resetService;
        this.redefinicaoSenhaService = redefinicaoSenhaService;
    }

    @Operation(
        summary = "Crie uma conta de usuário.",
        description = "Dadas as credenciais requisitadas, faça um cadastro de um Usuário simples no sistema.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Conta de usuário criada com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Usuário criado com sucesso.\", \"status\": 201}")
            )),
            @ApiResponse(responseCode = "409", description = "As credenciais informadas estão em conflito com dados já existentes no sistema.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Email já existente.\", \"status\": 409}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PostMapping("/cadastrar")
    public ResponseEntity<RequestResponseDTO> newUser(@RequestBody CreateUserDTO createUserDTO) {
        try {
            userService.registerNewUser(createUserDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Usuário criado com sucesso.", 201));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @PostMapping("/redefinicao-senha")
    public ResponseEntity<RequestResponseDTO> resetPassword(@RequestBody RedefinicaoSenhaDTO redefinicaoSenhaDTO) {
        try {
            redefinicaoSenhaService.redefinirSenha(redefinicaoSenhaDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Senha redefinida com sucesso!", 201));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @PostMapping("/esqueci-minha-senha")
    public ResponseEntity<RequestResponseDTO> forgotPassword(@RequestBody EsqueciMinhaSenhaDTO esqueciMinhaSenhaDTO) 
    {
        try {
            String email = esqueciMinhaSenhaDTO.emailRecuperacao();
            resetService.sendPasswordResetEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("E-mail de redefinição enviado com sucesso!\n\n Verifique sua caixa de entrada e spam.", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @SecurityRequirement(name = "Auth JWT")
    public ResponseEntity<List<User>> listaUsuarios() {
        List<User> users = userService.listAllUsers();
        return ResponseEntity.ok(users);
    }
}
