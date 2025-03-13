package com.faeterj.saudesafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.saudesafe.model.ReceitaMedicamento;

public interface ReceitaMedicamentoRepository extends JpaRepository<ReceitaMedicamento, Long>
{
    void deleteByReceitaReceitaId(Long receitaId);
}
