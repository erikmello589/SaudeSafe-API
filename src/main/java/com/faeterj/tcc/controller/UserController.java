package com.faeterj.tcc.controller;

import com.faeterj.tcc.dto.CreateUserDTO;
import com.faeterj.tcc.dto.EsqueciMinhaSenhaDTO;
import com.faeterj.tcc.dto.RequestResponseDTO;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.service.PasswordResetService;
import com.faeterj.tcc.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    private final PasswordResetService resetService;

    public UserController(UserService userService, PasswordResetService resetService) {
        this.userService = userService;
        this.resetService = resetService;
    }

    @PostMapping("/register")
    public ResponseEntity<RequestResponseDTO> newUser(@RequestBody CreateUserDTO createUserDTO) {
        try {
            userService.registerNewUser(createUserDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RequestResponseDTO("Usuário criado com sucesso.", 201));
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new RequestResponseDTO(e.getReason(), 409));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getReason(), 500));
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RequestResponseDTO(e.getReason(), 500));
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listaUsuarios() {
        List<User> users = userService.listAllUsers();
        return ResponseEntity.ok(users);
    }
}
