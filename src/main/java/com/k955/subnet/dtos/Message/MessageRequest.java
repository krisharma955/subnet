package com.k955.subnet.dtos.Message;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
        @NotBlank String content
) {
}
