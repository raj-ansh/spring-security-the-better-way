package com.example.jwt.dto;

public record LoginResponse(String token, String tokenType, long expiresIn) {
}
