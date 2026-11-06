package com.k955.subnet.dtos.MessageReaction;

import jakarta.validation.constraints.NotBlank;

public record MessageReactionRequest(
        @NotBlank String emoji
) {
}
