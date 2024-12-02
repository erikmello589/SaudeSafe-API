package com.faeterj.tcc.dto;

import java.time.Instant;

public record ReturnPacienteDTO(long pacienteId, String nomePaciente, String sobrenomePaciente, Instant dataCriacao) {

}
