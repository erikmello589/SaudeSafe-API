package com.faeterj.tcc.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_profissional_saude")
public class ProfissionalSaude 
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profissional_id")
    private UUID profissionalSaudeId;

    private String nomeProfissional;

    private String especialidadeProfissional;

    private String numeroClasseConselho;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "tb_profissionais_status",
        joinColumns = @JoinColumn(name = "profissional_id"),
        inverseJoinColumns = @JoinColumn(name = "status_id")
    )
    private StatusProfissional statusProfissional;

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

    public StatusProfissional getStatusProfissional() {
        return statusProfissional;
    }

    public void setStatusProfissional(StatusProfissional statusProfissional) {
        this.statusProfissional = statusProfissional;
    }

    public Instant getProfissionalDataCriacao() {
        return profissionalDataCriacao;
    }

    public void setProfissionalDataCriacao(Instant profissionalDataCriacao) {
        this.profissionalDataCriacao = profissionalDataCriacao;
    }

    
}
