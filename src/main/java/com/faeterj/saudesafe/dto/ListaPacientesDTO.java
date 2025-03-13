package com.faeterj.saudesafe.dto;

import java.util.List;

public record ListaPacientesDTO(List<ReturnPacienteDTO> listaPacientes, int page, int pageSize, int totalPages, long totalElements) {

}
