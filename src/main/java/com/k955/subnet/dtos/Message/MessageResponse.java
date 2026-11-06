package com.k955.subnet.dtos.Message;

import com.k955.subnet.dtos.Channel.ChannelResponse;
import com.k955.subnet.dtos.User.UserProfileResponse;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        ChannelResponse channel,
        UserProfileResponse sender,
        String content,
        ParentMessageResponse parentMessage,
        Instant createdAt,
        Instant updatedAt
) {
}
