package com.faeterj.tcc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.tcc.model.Atestado;

public interface AtestadoRepository extends JpaRepository<Atestado, Long> 
{
    Optional<Atestado> findByConsultaConsultaId(Long consultaId);
}
