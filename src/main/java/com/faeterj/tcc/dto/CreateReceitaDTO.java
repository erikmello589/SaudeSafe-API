package com.faeterj.tcc.dto;

import java.util.List;

public record CreateReceitaDTO(String observacaoReceita, List<CreateReceitaMedicamentoDTO> medicamentos) 
{

}
