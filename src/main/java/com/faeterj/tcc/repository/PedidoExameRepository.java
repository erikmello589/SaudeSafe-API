package com.faeterj.tcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.tcc.model.PedidoExame;

public interface PedidoExameRepository extends JpaRepository<PedidoExame, Long>
{
    //Optional<PedidoExame> findByConsultaConsultaId(Long consultaId);
}
