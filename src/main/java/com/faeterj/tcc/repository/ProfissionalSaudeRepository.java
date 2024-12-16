package com.faeterj.tcc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.faeterj.tcc.model.ProfissionalSaude;

@Repository
public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> 
{
    /**
     * Retorna uma página de profissionais associados.
     *
     * 
     * @param pageable Informações de paginação.
     * @return Página de profissionais.
     */
    Page<ProfissionalSaude> findAll(Pageable pageable);

}