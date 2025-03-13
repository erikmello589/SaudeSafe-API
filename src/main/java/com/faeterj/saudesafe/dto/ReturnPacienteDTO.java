package com.faeterj.saudesafe.dto;

import java.time.Instant;

public record ReturnPacienteDTO(long pacienteId, String nomePaciente, String sobrenomePaciente, Instant dataCriacao) {

}
