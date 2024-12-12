package com.faeterj.tcc.dto;

import java.time.Instant;

import com.faeterj.tcc.model.EstabelecimentoSaude;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.ProfissionalSaude;

public record ReturnConsultaPacienteDTO(long consultaId, Paciente paciente, 
ProfissionalSaude profissionalSaude, EstabelecimentoSaude estabelecimentoSaude, 
String motivoConsulta, String observacaoConsulta, Instant consultaData)
{

}
