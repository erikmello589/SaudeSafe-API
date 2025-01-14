package com.faeterj.tcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.tcc.model.ReceitaMedicamento;

public interface ReceitaMedicamentoRepository extends JpaRepository<ReceitaMedicamento, Long>
{
    void deleteByReceitaReceitaId(Long receitaId);
}
