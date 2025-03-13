package com.faeterj.saudesafe.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "tb_profissional_saude", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"numeroClasseConselho", "estadoProfissional"})
})
public class ProfissionalSaude 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "profissional_id")
    private Long profissionalSaudeId;

    private String nomeProfissional;
    private String especialidadeProfissional;
    private String numeroClasseConselho;
    private String estadoProfissional;

    @CreationTimestamp
    private Instant profissionalDataCriacao;

    private Long statusId;

    public Long getProfissionalSaudeId() {
        return profissionalSaudeId;
    }

    public void setProfissionalSaudeId(Long profissionalSaudeId) {
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

    public String getEstadoProfissional() {
        return estadoProfissional;
    }

    public void setEstadoProfissional(String estadoProfissional) {
        this.estadoProfissional = estadoProfissional;
    }

    public Instant getProfissionalDataCriacao() {
        return profissionalDataCriacao;
    }

    public void setProfissionalDataCriacao(Instant profissionalDataCriacao) {
        this.profissionalDataCriacao = profissionalDataCriacao;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }
    
}
