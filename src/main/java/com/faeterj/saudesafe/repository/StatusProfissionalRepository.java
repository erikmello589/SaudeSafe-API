package com.faeterj.saudesafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.saudesafe.model.StatusProfissional;

public interface StatusProfissionalRepository extends JpaRepository<StatusProfissional, Long>
{
    StatusProfissional findByProfissionalId(Long profissionalId);
}
