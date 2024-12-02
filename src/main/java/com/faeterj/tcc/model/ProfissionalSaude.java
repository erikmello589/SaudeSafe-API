package com.faeterj.tcc.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_profissional_saude")
public class ProfissionalSaude 
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID profissionalSaudeId;

    private String nomeProfissional;

    private String especialidadeProfissional;

    private String numeroClasseConselho;

    private boolean profissionalIsVerified;

    @CreationTimestamp
    private Instant profissionalDataCriacao;

    public UUID getProfissionalSaudeId() {
        return profissionalSaudeId;
    }

    public void setProfissionalSaudeId(UUID profissionalSaudeId) {
        this.profissionalSaudeId = profissionalSaudeId;
    }

    public String getNomeProfissional() {
        return nomeProfissional;
    }

    public void setNomeProfissional(String nomeProfissional) {
        this.nomeProfissional = nomeProfissional;
    }

    public String getEspecialidadeProfissional() {
        return especialidadeProfissional;
    }

    public void setEspecialidadeProfissional(String especialidadeProfissional) {
        this.especialidadeProfissional = especialidadeProfissional;
    }

    public String getNumeroClasseConselho() {
        return numeroClasseConselho;
    }

    public void setNumeroClasseConselho(String numeroClasseConselho) {
        this.numeroClasseConselho = numeroClasseConselho;
    }

    public boolean getProfissionalIsVerified() {
        return profissionalIsVerified;
    }

    public void setProfissionalIsVerified(boolean profissionalIsVerified) {
        this.profissionalIsVerified = profissionalIsVerified;
    }

    public Instant getProfissionalDataCriacao() {
        return profissionalDataCriacao;
    }

    public void setProfissionalDataCriacao(Instant profissionalDataCriacao) {
        this.profissionalDataCriacao = profissionalDataCriacao;
    }
}
