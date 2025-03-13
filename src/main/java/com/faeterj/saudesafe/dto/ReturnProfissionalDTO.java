package com.faeterj.saudesafe.dto;

import java.time.Instant;

import com.faeterj.saudesafe.model.StatusProfissional;

public record ReturnProfissionalDTO(Long profissionalId, String nomeProfissional, String especialidadeProfissional, String numeroClasseConselho, String estadoProfissional, StatusProfissional statusProfissional, Instant profissionalDataCriacao) {

}
