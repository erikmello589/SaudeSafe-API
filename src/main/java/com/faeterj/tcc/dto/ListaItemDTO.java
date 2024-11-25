package com.faeterj.tcc.dto;

import java.time.Instant;

//ListaItemDTO = Modelo de RETORNO de Paciente
public record ListaItemDTO(long pacienteId, String nomePaciente, String sobrenomePaciente, Instant dataCriacao) {

}
