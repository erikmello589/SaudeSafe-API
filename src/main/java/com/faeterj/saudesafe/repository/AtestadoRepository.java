package com.faeterj.saudesafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.saudesafe.model.Atestado;

public interface AtestadoRepository extends JpaRepository<Atestado, Long> 
{
    //Optional<Atestado> findByConsultaConsultaId(Long consultaId);
}
