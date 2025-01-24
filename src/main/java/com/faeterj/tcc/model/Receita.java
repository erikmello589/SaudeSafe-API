package com.faeterj.tcc.model;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tb_receitas")
public class Receita 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "receita_id")
    private Long receitaId;

    @OneToOne
    @JoinColumn(name = "consulta_id")
    @JsonIgnore
    private Consulta consulta;

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL)
    private List<ReceitaMedicamento> medicamentos;
   
    private String observacaoReceita;

    @NotNull
    private boolean temAnexo = false;

    @JsonIgnore
    private byte[] pdfAnexado;

    @CreationTimestamp
    private Instant receitaDataCriacao;

    public Long getReceitaId() {
        return receitaId;
    }

    public void setReceitaId(Long receitaId) {
        this.receitaId = receitaId;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public List<ReceitaMedicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<ReceitaMedicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getObservacaoReceita() {
        return observacaoReceita;
    }

    public void setObservacaoReceita(String observacaoReceita) {
        this.observacaoReceita = observacaoReceita;
    }

    public boolean getTemAnexo() {
        return temAnexo;
    }

    public void setTemAnexo(boolean temAnexo) {
        this.temAnexo = temAnexo;
    }

    public byte[] getPdfAnexado() {
        return pdfAnexado;
    }

    public void setPdfAnexado(byte[] pdfAnexado) {
        this.pdfAnexado = pdfAnexado;
    }

    public Instant getReceitaDataCriacao() {
        return receitaDataCriacao;
    }

    public void setReceitaDataCriacao(Instant receitaDataCriacao) {
        this.receitaDataCriacao = receitaDataCriacao;
    }
}