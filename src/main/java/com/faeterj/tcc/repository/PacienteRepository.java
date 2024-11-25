package com.faeterj.tcc.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.faeterj.tcc.model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

     /**
     * Retorna uma página de pacientes associados a um usuário específico.
     *
     * @param userID  O UUID do usuário.
     * @param pageable Informações de paginação.
     * @return Página de pacientes.
     */
    Page<Paciente> findByUserUserID(UUID userID, Pageable pageable);
}