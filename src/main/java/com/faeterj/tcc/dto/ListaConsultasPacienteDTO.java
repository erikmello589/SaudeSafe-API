package com.faeterj.tcc.dto;

import java.util.List;

public record ListaConsultasPacienteDTO(List<ReturnConsultaPacienteDTO> listaConsultasPaciente, int page, int pageSize, int totalPages, long totalElements) {

}
