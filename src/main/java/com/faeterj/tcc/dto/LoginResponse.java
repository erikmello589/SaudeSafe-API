package com.faeterj.tcc.dto;

public record LoginResponse(String accessToken, Long expiresIn, String refreshToken) {

}
