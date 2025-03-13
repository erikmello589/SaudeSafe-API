package com.faeterj.saudesafe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_medicamento_receita")
public class ReceitaMedicamento 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "medicamento_id")
    private Long medicamentoId;

    @ManyToOne
    @JoinColumn(name = "receita_id")
    @JsonIgnore
    private Receita receita;

    private String nomeMedicamento;

    private String doseMedicamento;

    private String qtdDoseMedicamento;

    private String intervaloMedicamento;

    private String periodoMedicamento;

    private String observacaoMedicamento;

    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public String getNomeMedicamento() {
        return nomeMedicamento;
    }

    public void setNomeMedicamento(String nomeMedicamento) {
        this.nomeMedicamento = nomeMedicamento;
    }

    public String getDoseMedicamento() {
        return doseMedicamento;
    }

    public void setDoseMedicamento(String doseMedicamento) {
        this.doseMedicamento = doseMedicamento;
    }

    public String getQtdDoseMedicamento() {
        return qtdDoseMedicamento;
    }

    public void setQtdDoseMedicamento(String qtdDoseMedicamento) {
        this.qtdDoseMedicamento = qtdDoseMedicamento;
    }

    public String getIntervaloMedicamento() {
        return intervaloMedicamento;
    }

    public void setIntervaloMedicamento(String intervaloMedicamento) {
        this.intervaloMedicamento = intervaloMedicamento;
    }

    public String getPeriodoMedicamento() {
        return periodoMedicamento;
    }

    public void setPeriodoMedicamento(String periodoMedicamento) {
        this.periodoMedicamento = periodoMedicamento;
    }

    public String getObservacaoMedicamento() {
        return observacaoMedicamento;
    }

    public void setObservacaoMedicamento(String observacaoMedicamento) {
        this.observacaoMedicamento = observacaoMedicamento;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }
}
