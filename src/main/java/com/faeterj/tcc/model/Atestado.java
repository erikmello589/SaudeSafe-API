package com.faeterj.tcc.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_atestados")
public class Atestado 
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "atestado_id")
    private Long atestadoId;

    @OneToOne
    @JoinColumn(name = "consulta_id")
    @JsonIgnore
    private Consulta consulta;

    private byte[] pdfAnexado;

    private String periodoAfastamento;

    private String observacaoAtestado;

    @CreationTimestamp
    private Instant atestadoDataCriacao;

    public Long getAtestadoId() {
        return atestadoId;
    }

    public void setAtestadoId(Long atestadoId) {
        this.atestadoId = atestadoId;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public byte[] getPdfAnexado() {
        return pdfAnexado;
    }

    public void setPdfAnexado(byte[] pdfAnexado) {
        this.pdfAnexado = pdfAnexado;
    }

    public String getPeriodoAfastamento() {
        return periodoAfastamento;
    }

    public void setPeriodoAfastamento(String periodoAfastamento) {
        this.periodoAfastamento = periodoAfastamento;
    }

    public String getObservacaoAtestado() {
        return observacaoAtestado;
    }

    public void setObservacaoAtestado(String observacaoAtestado) {
        this.observacaoAtestado = observacaoAtestado;
    }

    public Instant getAtestadoDataCriacao() {
        return atestadoDataCriacao;
    }

    public void setAtestadoDataCriacao(Instant atestadoDataCriacao) {
        this.atestadoDataCriacao = atestadoDataCriacao;
    }

}
