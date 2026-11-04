package com.k955.subnet.dtos.Channel;

import com.k955.subnet.enums.Channel.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateChannelRequest(
        @NotBlank String name,
        String description,
        @NotNull Type type
) {
}
