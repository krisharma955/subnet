package com.k955.subnet.dtos.MessageReaction;

import com.k955.subnet.dtos.Message.MessageResponse;
import com.k955.subnet.dtos.User.UserProfileResponse;

import java.time.Instant;
import java.util.UUID;

public record MessageReactionResponse(
        UUID id,
        MessageResponse message,
        UserProfileResponse user,
        String emoji,
        Instant createdAt,
        Instant updatedAt
) {
}
