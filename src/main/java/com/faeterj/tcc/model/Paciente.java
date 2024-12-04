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
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_pacientes")
public class Paciente 
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "paciente_id")
    private Long pacienteId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String nomePaciente;

    private String sobrenomePaciente;

    @CreationTimestamp
    private Instant dataCriacao; //Data e hora de criação do paciente pelo usuário (Gerado automatico pelo sistema)

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Instant dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getSobrenomePaciente() {
        return sobrenomePaciente;
    }

    public void setSobrenomePaciente(String sobrenomePaciente) {
        this.sobrenomePaciente = sobrenomePaciente;
    }

    
}
