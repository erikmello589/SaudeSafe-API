package com.faeterj.tcc.repository;

import com.faeterj.tcc.model.ProfissionalConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfissionalConsultaRepository extends JpaRepository<ProfissionalConsulta, Long> 
{
    
}
