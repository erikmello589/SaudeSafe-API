package com.faeterj.saudesafe.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.faeterj.saudesafe.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUserUserID(UUID userId);
}

