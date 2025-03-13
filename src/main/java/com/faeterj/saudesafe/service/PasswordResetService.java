package com.faeterj.saudesafe.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.saudesafe.model.PasswordResetToken;
import com.faeterj.saudesafe.model.User;
import com.faeterj.saudesafe.repository.PasswordResetTokenRepository;
import com.faeterj.saudesafe.repository.UserRepository;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    public void sendPasswordResetEmail(String email) {

        // Localizar o usuário pelo e-mail
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        PasswordResetToken resetToken = tokenRepository.findByUserUserID(user.getUserID());
        
        //caso o usuário já tenha um token de recuperação, exclua o antigo
        if (resetToken != null)
        {
            tokenRepository.delete(resetToken);
        }

        // Gerar um token novo
        String token = UUID.randomUUID().toString();
        resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Expira em 1 hora

        tokenRepository.save(resetToken);

        // Enviar e-mail
        String resetUrl = "http://localhost:8080/redefinicao-senha?token=" + token;
        sendEmail(user.getEmail(), resetUrl);
    }

    private void sendEmail(String to, String resetUrl) 
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("saudesafeapp@gmail.com");
        message.setTo(to);
        message.setSubject("Redefinição de Senha");
        message.setText("Para redefinir sua senha, clique no link:\n\n " + resetUrl);
        mailSender.send(message);
    }
}
