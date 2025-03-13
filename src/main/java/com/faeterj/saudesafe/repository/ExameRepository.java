package com.faeterj.saudesafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.saudesafe.model.Exame;

public interface ExameRepository extends JpaRepository<Exame, Long> 
{
    void deleteByPedidoExamePedidoExameId(Long pedidoExameId);
}
