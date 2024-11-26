package com.faeterj.tcc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.faeterj.tcc.dto.EsqueciMinhaSenhaDTO;
import com.faeterj.tcc.service.PasswordResetService;

@RestController
public class EsqueciMinhaSenhaController 
{
    @Autowired
    private PasswordResetService resetService;

    @PostMapping("/esqueci-minha-senha")
    public ResponseEntity<String> forgotPassword(@RequestBody EsqueciMinhaSenhaDTO esqueciMinhaSenhaDTO) {
        String email = esqueciMinhaSenhaDTO.emailRecuperacao();
        resetService.sendPasswordResetEmail(email);
        return ResponseEntity.ok("E-mail de redefinição enviado com sucesso!\n\n Verifique sua caixa de entrada e spam.");
    }
}
