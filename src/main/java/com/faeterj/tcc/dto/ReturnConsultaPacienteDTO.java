package com.faeterj.tcc.dto;

import java.time.Instant;

import com.faeterj.tcc.model.EstabelecimentoSaude;
import com.faeterj.tcc.model.Paciente;

public record ReturnConsultaPacienteDTO(long consultaId, Paciente paciente, 
ReturnProfissionalDTO profissionalConsulta, EstabelecimentoSaude estabelecimentoSaude, 
String motivoConsulta, String observacaoConsulta, Instant consultaData)
{

}
