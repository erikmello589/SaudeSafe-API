package com.faeterj.tcc.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_estabelecimento_saude")
public class EstabelecimentoSaude 
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "estabelecimento_id")
    private Long estabelecimentoId;

    private String nomeEstabelecimento;

    private String cepEstabelecimento;

    private String enderecoEstabelecimento;
    
    @CreationTimestamp
    private Instant estabelecimentoDataCriacao;

    public Long getEstabelecimentoId() {
        return estabelecimentoId;
    }

    public void setEstabelecimentoId(Long estabelecimentoId) {
        this.estabelecimentoId = estabelecimentoId;
    }

    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }

    public void setNomeEstabelecimento(String nomeEstabelecimento) {
        this.nomeEstabelecimento = nomeEstabelecimento;
    }

    public String getCepEstabelecimento() {
        return cepEstabelecimento;
    }

    public void setCepEstabelecimento(String cepEstabelecimento) {
        this.cepEstabelecimento = cepEstabelecimento;
    }

    public String getEnderecoEstabelecimento() {
        return enderecoEstabelecimento;
    }

    public void setEnderecoEstabelecimento(String enderecoEstabelecimento) {
        this.enderecoEstabelecimento = enderecoEstabelecimento;
    }

    public Instant getEstabelecimentoDataCriacao() {
        return estabelecimentoDataCriacao;
    }

    public void setEstabelecimentoDataCriacao(Instant estabelecimentoDataCriacao) {
        this.estabelecimentoDataCriacao = estabelecimentoDataCriacao;
    }
}
