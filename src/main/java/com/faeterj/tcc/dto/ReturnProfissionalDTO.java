package com.faeterj.tcc.dto;

import java.time.Instant;
import java.util.UUID;

import com.faeterj.tcc.model.StatusProfissional;

public record ReturnProfissionalDTO(UUID profissionalId, String nomeProfissional, String especialidadeProfissional, String numeroClasseConselho, String estadoProfissional, StatusProfissional statusProfissional, Instant profissionalDataCriacao) {

}
