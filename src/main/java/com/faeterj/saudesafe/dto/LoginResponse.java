package com.faeterj.saudesafe.dto;

public record LoginResponse(String accessToken, Long expiresIn, String refreshToken) {

}
