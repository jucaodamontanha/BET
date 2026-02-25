package com.bet.platform.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}