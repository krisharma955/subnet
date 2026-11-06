package com.k955.subnet.dtos.Message;

import com.k955.subnet.dtos.User.UserProfileResponse;

import java.time.Instant;
import java.util.UUID;

public record ParentMessageResponse(
        UUID id,
        UserProfileResponse sender,
        String content,
        Instant createdAt
) {
}
