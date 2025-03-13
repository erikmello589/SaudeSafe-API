package com.faeterj.saudesafe.dto;

import java.time.Instant;

import com.faeterj.saudesafe.model.EstabelecimentoSaude;
import com.faeterj.saudesafe.model.Paciente;

public record ReturnConsultaDTO(long consultaId, Paciente paciente, 
ReturnProfissionalDTO profissionalConsulta, EstabelecimentoSaude estabelecimentoSaude, 
String motivoConsulta, String observacaoConsulta, Instant consultaData)
{

}
