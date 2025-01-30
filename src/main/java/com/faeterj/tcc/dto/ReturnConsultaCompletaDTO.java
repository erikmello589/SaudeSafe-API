package com.faeterj.tcc.dto;

import java.time.Instant;

import com.faeterj.tcc.model.Atestado;
import com.faeterj.tcc.model.EstabelecimentoSaude;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.PedidoExame;
import com.faeterj.tcc.model.Receita;

public record ReturnConsultaCompletaDTO(long consultaId, Paciente paciente, 
ReturnProfissionalDTO profissionalConsulta, EstabelecimentoSaude estabelecimentoSaude, 
String motivoConsulta, String observacaoConsulta, Instant consultaData, Atestado atestado, Receita receita, PedidoExame pedidoExame) {

}
