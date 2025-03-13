package com.faeterj.saudesafe.dto;

public record CreateReceitaMedicamentoDTO(String nomeMedicamento, String doseMedicamento, 
                                          String qtdDoseMedicamento, String intervaloMedicamento,
                                          String periodoMedicamento, String observacaoMedicamento) {

}
