package com.faeterj.saudesafe.dto;

import java.util.List;

public record CreateReceitaDTO(String observacaoReceita, List<CreateReceitaMedicamentoDTO> medicamentos) 
{

}
