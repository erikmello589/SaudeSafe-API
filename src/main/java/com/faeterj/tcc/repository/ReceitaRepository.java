package com.faeterj.tcc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.tcc.model.Receita;

public interface ReceitaRepository extends JpaRepository<Receita, Long>
{
    Optional<Receita> findByConsultaConsultaId(Long consultaId);
}
