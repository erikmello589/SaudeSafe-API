package com.faeterj.tcc.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestEnvVariables implements CommandLineRunner {

    @Autowired
    private Dotenv dotenv;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Banco de Dados: " + dotenv.get("DB_URL"));
        System.out.println("Usuário do Banco: " + dotenv.get("DB_USERNAME"));
        System.out.println("E-mail SMTP: " + dotenv.get("MAIL_HOST"));
    }
}