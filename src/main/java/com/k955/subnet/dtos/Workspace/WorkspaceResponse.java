package com.k955.subnet.dtos.Workspace;

import com.k955.subnet.dtos.User.UserProfileResponse;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceResponse(
        UUID id,
        String name,
        String description,
        String slug,
        UserProfileResponse createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}
