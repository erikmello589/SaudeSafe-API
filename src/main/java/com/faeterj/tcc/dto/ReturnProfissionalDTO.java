package com.faeterj.tcc.dto;

import java.time.Instant;
import java.util.UUID;

public record ReturnProfissionalDTO(UUID profissionalId, String nomeProfissional, String especialidadeProfissional, String numeroClasseConselho, boolean profissionalIsVerified, Instant profissionalDataCriacao) {

}
