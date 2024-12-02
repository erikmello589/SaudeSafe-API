package com.faeterj.tcc.dto;

import java.util.List;

public record ListaProfissionaisDTO(List<ReturnProfissionalDTO> listaProfissionais, int page, int pageSize, int totalPages, long totalElements) {

}
