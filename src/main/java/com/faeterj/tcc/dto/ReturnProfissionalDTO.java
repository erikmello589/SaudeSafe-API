package com.faeterj.tcc.dto;

import java.time.Instant;

import com.faeterj.tcc.model.StatusProfissional;

public record ReturnProfissionalDTO(Long profissionalId, String nomeProfissional, String especialidadeProfissional, String numeroClasseConselho, String estadoProfissional, StatusProfissional statusProfissional, Instant profissionalDataCriacao) {

}
