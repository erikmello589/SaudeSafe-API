package com.faeterj.saudesafe.dto;

import java.time.Instant;

public record CreateConsultaDTO(String nomeProfissional, String especialidadeProfissional, String numeroClasseConselho, String estadoProfissional, String nomeEstabelecimento, String cepEstabelecimento, String enderecoEstabelecimento, String motivoConsulta, String observacaoConsulta, Instant consultaData) {

}
