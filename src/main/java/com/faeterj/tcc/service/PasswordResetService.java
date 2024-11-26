package com.faeterj.tcc.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.faeterj.tcc.model.PasswordResetToken;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.PasswordResetTokenRepository;
import com.faeterj.tcc.repository.UserRepository;

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
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Gerar o token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
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
        message.setFrom("noreply5TFC@gmail.com");
        message.setTo(to);
        message.setSubject("Redefinição de Senha");
        message.setText("Para redefinir sua senha, clique no link:\n\n " + resetUrl);
        mailSender.send(message);
    }
}
