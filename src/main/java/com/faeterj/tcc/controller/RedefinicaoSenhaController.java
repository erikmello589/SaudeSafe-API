package com.faeterj.tcc.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.faeterj.tcc.dto.RedefinicaoSenhaDTO;
import com.faeterj.tcc.model.PasswordResetToken;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.PasswordResetTokenRepository;
import com.faeterj.tcc.repository.UserRepository;

@RestController
public class RedefinicaoSenhaController {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/redefinicao-senha")
    public ResponseEntity<String> resetPassword(@RequestBody RedefinicaoSenhaDTO redefinicaoSenhaDTO) 
    {
        String token = redefinicaoSenhaDTO.token();
        String newPassword = redefinicaoSenhaDTO.newPassword();

        PasswordResetToken resetToken = tokenRepository.findByToken(token);

        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token inválido ou expirado");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken); // Remover o token usado

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}

