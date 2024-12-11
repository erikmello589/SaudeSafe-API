package com.faeterj.tcc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tb_status_profissional")
public class StatusProfissional {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "status_id")
    private Long statusId;

    @OneToOne
    @JoinColumn(name = "profissional_id")
    @JsonIgnore  
    private ProfissionalSaude profissionalSaude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusEnum status;

    @UpdateTimestamp
    @Column(name = "ultima_modificacao", nullable = false)
    private Instant ultimaModificacao;

    public ProfissionalSaude getProfissionalSaude() {
        return profissionalSaude;
    }

    public void setProfissionalSaude(ProfissionalSaude profissionalSaude) {
        this.profissionalSaude = profissionalSaude;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Instant getUltimaModificacao() {
        return ultimaModificacao;
    }

    public void setUltimaModificacao(Instant ultimaModificacao) {
        this.ultimaModificacao = ultimaModificacao;
    }
}
