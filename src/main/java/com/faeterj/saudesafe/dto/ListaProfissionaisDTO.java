package com.faeterj.saudesafe.dto;

import java.util.List;

public record ListaProfissionaisDTO(List<ReturnProfissionalDTO> listaProfissionais, int page, int pageSize, int totalPages, long totalElements) {

}
