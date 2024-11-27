package com.faeterj.tcc.service;

import com.faeterj.tcc.dto.LoginRequest;
import com.faeterj.tcc.dto.LoginResponse;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenService(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        Optional<User> user = loginRequest.usernameOrEmail().contains("@")
                ? userRepository.findByEmail(loginRequest.usernameOrEmail())
                : userRepository.findByUsername(loginRequest.usernameOrEmail());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("Usuário ou senha inválida");
        }

        return generateTokens(user.get());
    }

    public LoginResponse refreshToken(JwtAuthenticationToken refreshToken) {
        User user = userRepository.findById(UUID.fromString(refreshToken.getName()))
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        return generateTokens(user);
    }

    private LoginResponse generateTokens(User user) {
        var now = Instant.now();
        var accessTokenExpiresIn = 900L; // 15 minutos

        // Gerar Access Token (contém apenas informações mínimas)
        String accessTokenScopes = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.getUserID().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpiresIn))
                .claim("scope", accessTokenScopes)
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();

        // Gerar Refresh Token (informações mínimas e separadas)
        var refreshTokenExpiresIn = 1296000L; // 15 dias
        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.getUserID().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenExpiresIn))
                .build();

        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        return new LoginResponse(accessToken, accessTokenExpiresIn, refreshToken);
    }
}
