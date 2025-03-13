package com.faeterj.saudesafe.controller;

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

import com.faeterj.saudesafe.dto.CreateUserDTO;
import com.faeterj.saudesafe.dto.EsqueciMinhaSenhaDTO;
import com.faeterj.saudesafe.dto.RedefinicaoSenhaDTO;
import com.faeterj.saudesafe.dto.RequestResponseDTO;
import com.faeterj.saudesafe.model.User;
import com.faeterj.saudesafe.service.PasswordResetService;
import com.faeterj.saudesafe.service.RedefinicaoSenhaService;
import com.faeterj.saudesafe.service.UserService;

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

    @Operation(
        summary = "Informe e altere sua senha de usuário.",
        description = "Endpoint para fazer a alteração de senha. Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Senha Redefinida com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Senha Redefinida com sucesso.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "400", description = "Token Inválido ou Expirado.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Token Inválido ou Expirado.\", \"status\": 400}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PostMapping("/redefinicao-senha")
    public ResponseEntity<RequestResponseDTO> resetPassword(@RequestBody RedefinicaoSenhaDTO redefinicaoSenhaDTO) {
        try {
            redefinicaoSenhaService.redefinirSenha(redefinicaoSenhaDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new RequestResponseDTO("Senha redefinida com sucesso!", 200));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new RequestResponseDTO(e.getReason(), e.getStatusCode().value()));
        }
    }

    @Operation(
        summary = "Requisite o Email de Recuperação de Senha.",
        description = "Endpoint para fazer a requisição do email de recuperação de senha. Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Email de Recuperação Enviado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"E-mail de redefinição enviado com sucesso! Verifique sua caixa de entrada e spam.\", \"status\": 200}")
            )),
            @ApiResponse(responseCode = "404", description = "Não há conta vinculada ao Email informado.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Usuário não encontrado.\", \"status\": 404}")
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RequestResponseDTO.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
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

    @Operation(
        summary = "Visualize todas as contas de usuário.",
        description = "Endpoint de cunho administrativo para fiscalização de todas as contas de usuário no sistema. Endpoint Restrito apenas a usuários administradores logados."
    )
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @SecurityRequirement(name = "Auth JWT")
    public ResponseEntity<List<User>> listaUsuarios() {
        List<User> users = userService.listAllUsers();
        return ResponseEntity.ok(users);
    }
}
