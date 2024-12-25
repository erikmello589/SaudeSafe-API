package com.faeterj.tcc.dto;

import java.util.List;

public record ListaConsultasDTO(List<ReturnConsultaDTO> listaConsultasPaciente, int page, int pageSize, int totalPages, long totalElements) {

}
