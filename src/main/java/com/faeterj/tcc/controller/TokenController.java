package com.faeterj.tcc.controller;

import com.faeterj.tcc.dto.LoginRequest;
import com.faeterj.tcc.dto.LoginResponse;
import com.faeterj.tcc.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(
        summary = "Acesse a sua conta de usuário.",
        description = "Dada as credenciais de acesso, acesse o sistema e suas funcionalidades.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login Realizado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )),
            @ApiResponse(responseCode = "401", description = "Usuário ou senha inválida.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = tokenService.authenticate(loginRequest);
            return ResponseEntity.ok(response); // Retorna 200 OK
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, null)); // Retorna 401 Unauthorized
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(null, null, null)); // Retorna 500 Internal Server Error
        }
    }

    @Operation(
        summary = "Acesse a sua conta de usuário.",
        description = "Dada as credenciais de acesso, acesse o sistema e suas funcionalidades.\n Endpoint Público a todos os visitantes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login Realizado com sucesso.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )),
            @ApiResponse(responseCode = "401", description = "Usuário ou senha inválida.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(value = "{\"message\": \"Erro interno no servidor.\", \"status\": 500}")
            ))
        }
    )
    @PostMapping("/refresh-login")
    public ResponseEntity<LoginResponse> refreshToken(JwtAuthenticationToken refreshTokenRecebido) {
        try {
            LoginResponse response = tokenService.refreshToken(refreshTokenRecebido);
            return ResponseEntity.ok(response); // Retorna 200 OK
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, null)); // Retorna 401 Unauthorized
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(null, null, null)); // Retorna 500 Internal Server Error
        }
    }

    // Manipulação de exceções para fornecer respostas detalhadas
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + ex.getMessage());
    }
}
