package com.k955.subnet.dtos.User;

import com.k955.subnet.enums.User.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        String email,
        UserStatus status,
        Instant createdAt
) {
}
