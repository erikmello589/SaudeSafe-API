package com.faeterj.tcc.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.faeterj.tcc.model.StatusProfissional;

public interface StatusProfissionalRepository extends JpaRepository<StatusProfissional, Long>
{
    StatusProfissional findByProfissionalSaudeProfissionalSaudeId(UUID profissionalId);
}
