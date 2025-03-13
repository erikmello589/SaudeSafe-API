package com.faeterj.saudesafe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.saudesafe.model.Consulta;
import com.faeterj.saudesafe.model.User;
public interface ConsultaRepository extends JpaRepository<Consulta, Long>
{
    /**
     * Retorna uma página de consultas associadas a um paciente específico.
     *
     * @param pacienteID  O id do paciente.
     * @param pageable Informações de paginação.
     * @return Página de pacientes.
     */
    Page<Consulta> findByPacientePacienteId(Long pacienteID, Pageable pageable);

    /**
     * Retorna uma página de consultas associadas ao usuario autenticado.
     * (Desenvolva aqui essa função)
     *
     * @param User  O usuario desejado.
     * @param pageable Informações de paginação.
     * @return Página de pacientes.
     */
    Page<Consulta> findByPacienteUser(User user, Pageable pageable);
}
