package com.faeterj.saudesafe.dto;

import java.time.Instant;

import com.faeterj.saudesafe.model.Atestado;
import com.faeterj.saudesafe.model.EstabelecimentoSaude;
import com.faeterj.saudesafe.model.Paciente;
import com.faeterj.saudesafe.model.PedidoExame;
import com.faeterj.saudesafe.model.Receita;

public record ReturnConsultaCompletaDTO(long consultaId, Paciente paciente, 
ReturnProfissionalDTO profissionalConsulta, EstabelecimentoSaude estabelecimentoSaude, 
String motivoConsulta, String observacaoConsulta, Instant consultaData, Atestado atestado, Receita receita, PedidoExame pedidoExame) {

}
