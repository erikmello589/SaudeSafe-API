package com.faeterj.saudesafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.saudesafe.model.Receita;

public interface ReceitaRepository extends JpaRepository<Receita, Long>
{
    //Optional<Receita> findByConsultaConsultaId(Long consultaId);
}
