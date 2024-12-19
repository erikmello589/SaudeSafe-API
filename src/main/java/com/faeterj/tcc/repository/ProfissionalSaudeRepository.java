package com.faeterj.tcc.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("SELECT p FROM ProfissionalSaude p WHERE TYPE(p) = ProfissionalSaude")
    Page<ProfissionalSaude> findAll(Pageable pageable);

    /**
     * Retorna um profissional dado seu CRM e Estado/UF.
     *
     * 
     * @param numeroClasseConselho numero do CRM.
     * @param estadoProfissional estado/UF do do profissional desejado.
     * @return <optional> Profissional desejado.
     */
    @Query("SELECT p FROM ProfissionalSaude p WHERE TYPE(p) = ProfissionalSaude AND p.numeroClasseConselho = :numeroClasseConselho AND p.estadoProfissional = :estadoProfissional")
    Optional<ProfissionalSaude> findByNumeroClasseConselhoAndEstadoProfissional(
        @Param("numeroClasseConselho") String numeroClasseConselho, 
        @Param("estadoProfissional") String estadoProfissional
    );

}