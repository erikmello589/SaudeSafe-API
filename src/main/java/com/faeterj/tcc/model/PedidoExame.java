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
@Table(name = "tb_pedido_exames")
public class PedidoExame 
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "pedidoExame_id")
    private Long pedidoExameId;

    @OneToOne
    @JoinColumn(name = "consulta_id")
    @JsonIgnore
    private Consulta consulta;

    @NotNull
    private boolean temAnexo = false;

    @JsonIgnore
    private byte[] pdfAnexado;

    @NotNull
    private String tipoAnexo = "";

    @OneToMany(mappedBy = "pedidoExame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exame> exames;

    @CreationTimestamp
    private Instant pedidoDataCriacao;

    private String pedidoObservacao;

    public Long getPedidoExameId() {
        return pedidoExameId;
    }

    public void setPedidoExameId(Long pedidoExameId) {
        this.pedidoExameId = pedidoExameId;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
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

    public String getTipoAnexo() {
        return tipoAnexo;
    }

    public void setTipoAnexo(String tipoAnexo) {
        this.tipoAnexo = tipoAnexo;
    }

    public List<Exame> getExames() {
        return exames;
    }

    public void setExames(List<Exame> exames) {
        this.exames = exames;
    }

    public Instant getPedidoDataCriacao() {
        return pedidoDataCriacao;
    }

    public void setPedidoDataCriacao(Instant pedidoDataCriacao) {
        this.pedidoDataCriacao = pedidoDataCriacao;
    }

    public String getPedidoObservacao() {
        return pedidoObservacao;
    }

    public void setPedidoObservacao(String pedidoObservacao) {
        this.pedidoObservacao = pedidoObservacao;
    }
}
