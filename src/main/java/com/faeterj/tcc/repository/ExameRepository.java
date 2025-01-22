package com.faeterj.tcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.tcc.model.Exame;

public interface ExameRepository extends JpaRepository<Exame, Long> 
{
    void deleteByPedidoExamePedidoExameId(Long pedidoExameId);
}
