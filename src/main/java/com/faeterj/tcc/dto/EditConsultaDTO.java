package com.faeterj.tcc.dto;

import java.time.Instant;

public record EditConsultaDTO(Long pacienteId, String nomeEstabelecimento, String cepEstabelecimento, String enderecoEstabelecimento, String motivoConsulta, String observacaoConsulta, Instant consultaData) {

}
