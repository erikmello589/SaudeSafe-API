package com.faeterj.saudesafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.saudesafe.model.PedidoExame;

public interface PedidoExameRepository extends JpaRepository<PedidoExame, Long>
{
    //Optional<PedidoExame> findByConsultaConsultaId(Long consultaId);
}
