package com.faeterj.saudesafe.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_consultas")
public class Consulta 
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "consulta_id")
    private Long consultaId;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profissional_id")
    private ProfissionalConsulta profissionalConsulta;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "estabelecimento_id")
    private EstabelecimentoSaude estabelecimentoSaude;

    private String motivoConsulta;

    private String observacaoConsulta;

    private Instant consultaData;

    @CreationTimestamp
    private Instant consultaDataCriacao;

    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Atestado atestado;

    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Receita receita;

    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private PedidoExame pedidoExame;

    public Atestado getAtestado() {
        return atestado;
    }

    public void setAtestado(Atestado atestado) {
        this.atestado = atestado;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public PedidoExame getPedidoExame() {
        return pedidoExame;
    }

    public void setPedidoExame(PedidoExame pedidoExame) {
        this.pedidoExame = pedidoExame;
    }

    public Long getConsultaId() {
        return consultaId;
    }

    public void setConsultaId(Long consultaId) {
        this.consultaId = consultaId;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public ProfissionalConsulta getProfissionalConsulta() {
        return profissionalConsulta;
    }

    public void setProfissionalConsulta(ProfissionalConsulta profissionalConsulta) {
        this.profissionalConsulta = profissionalConsulta;
    }

    public EstabelecimentoSaude getEstabelecimentoSaude() {
        return estabelecimentoSaude;
    }

    public void setEstabelecimentoSaude(EstabelecimentoSaude estabelecimentoSaude) {
        this.estabelecimentoSaude = estabelecimentoSaude;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getObservacaoConsulta() {
        return observacaoConsulta;
    }

    public void setObservacaoConsulta(String observacaoConsulta) {
        this.observacaoConsulta = observacaoConsulta;
    }

    public Instant getConsultaData() {
        return consultaData;
    }

    public void setConsultaData(Instant consultaData) {
        this.consultaData = consultaData;
    }

    public Instant getConsultaDataCriacao() {
        return consultaDataCriacao;
    }

    public void setConsultaDataCriacao(Instant consultaDataCriacao) {
        this.consultaDataCriacao = consultaDataCriacao;
    }
}
