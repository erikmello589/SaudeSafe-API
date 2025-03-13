package com.faeterj.saudesafe.repository;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.faeterj.saudesafe.model.ProfissionalConsulta;

@Repository
public interface ProfissionalConsultaRepository extends JpaRepository<ProfissionalConsulta, Long> 
{
    @Modifying
    @Transactional
    @Query("UPDATE ProfissionalConsulta p SET p.nomeProfissional = :nomeProfissional, p.especialidadeProfissional = :especialidadeProfissional, p.numeroClasseConselho = :numeroClasseConselho, p.estadoProfissional = :estadoProfissional WHERE TYPE(p) = ProfissionalConsulta AND p.statusId = :statusId")
    int atualizaCrmProfissionaisConsulta(@Param("nomeProfissional") String nomeProfissional,
                                         @Param("especialidadeProfissional") String especialidadeProfissional,
                                         @Param("numeroClasseConselho") String numeroClasseConselho, 
                                         @Param("estadoProfissional") String estadoProfissional,
                                         @Param("statusId") Long statusId);
}
