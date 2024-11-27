package com.faeterj.tcc.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.tcc.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUserUserID(UUID userId);
}

