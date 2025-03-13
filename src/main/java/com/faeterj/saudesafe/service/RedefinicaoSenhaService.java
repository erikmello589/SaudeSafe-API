package com.faeterj.saudesafe.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.saudesafe.dto.RedefinicaoSenhaDTO;
import com.faeterj.saudesafe.model.PasswordResetToken;
import com.faeterj.saudesafe.model.User;
import com.faeterj.saudesafe.repository.PasswordResetTokenRepository;
import com.faeterj.saudesafe.repository.UserRepository;

@Service
public class RedefinicaoSenhaService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public RedefinicaoSenhaService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void redefinirSenha(RedefinicaoSenhaDTO redefinicaoSenhaDTO) {
        String token = redefinicaoSenhaDTO.token();
        String newPassword = redefinicaoSenhaDTO.newPassword();

        // Buscar o token de redefinição de senha
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token Inválido.");
        }

        // Verificar se o token está expirado
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token Expirado.");
        }

        // Redefinir a senha do usuário
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Excluir o token usado
        tokenRepository.delete(resetToken);
    }
}
