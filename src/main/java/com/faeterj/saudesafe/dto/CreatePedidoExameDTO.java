package com.faeterj.saudesafe.dto;

import java.util.List;

public record CreatePedidoExameDTO(String pedidoObservacao, List<CreateExameDTO> exames) {

}
