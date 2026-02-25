package com.bet.platform.user.dto;

import com.bet.platform.user.model.Role;
import com.bet.platform.user.model.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role,
        UserStatus status
) {}