package com.faeterj.tcc.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissionalSaude;

    @OneToOne
    @JoinColumn(name = "estabelecimento_id")
    private EstabelecimentoSaude estabelecimentoSaude;

    private String motivoConsulta;

    private String observacaoConsulta;

    private Instant consultaData;

    @CreationTimestamp
    private Instant consultaDataCriacao;

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

    public ProfissionalSaude getProfissionalSaude() {
        return profissionalSaude;
    }

    public void setProfissionalSaude(ProfissionalSaude profissionalSaude) {
        this.profissionalSaude = profissionalSaude;
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
