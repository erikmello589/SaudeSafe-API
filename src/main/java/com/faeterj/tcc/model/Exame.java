package com.faeterj.tcc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_exames")
public class Exame 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "exame_id")
    private Long exameId;

    @ManyToOne
    @JoinColumn(name = "pedidoExame_id")
    @JsonIgnore
    private PedidoExame pedidoExame;

    private String tipoExame;

    private String nomeExame;

    private String observacaoExame;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_exame", nullable = false)
    private SituacaoEnum situacaoExame;

    public Long getExameId() {
        return exameId;
    }

    public void setExameId(Long exameId) {
        this.exameId = exameId;
    }

    public PedidoExame getPedidoExame() {
        return pedidoExame;
    }

    public void setPedidoExame(PedidoExame pedidoExame) {
        this.pedidoExame = pedidoExame;
    }

    public String getTipoExame() {
        return tipoExame;
    }

    public void setTipoExame(String tipoExame) {
        this.tipoExame = tipoExame;
    }

    public String getNomeExame() {
        return nomeExame;
    }

    public void setNomeExame(String nomeExame) {
        this.nomeExame = nomeExame;
    }

    public String getObservacaoExame() {
        return observacaoExame;
    }

    public void setObservacaoExame(String observacaoExame) {
        this.observacaoExame = observacaoExame;
    }

    public SituacaoEnum getSituacaoExame() {
        return situacaoExame;
    }

    public void setSituacaoExame(SituacaoEnum situacaoExame) {
        this.situacaoExame = situacaoExame;
    }
}
