package com.k955.subnet.dtos.ChannelJoinRequest;

import com.k955.subnet.dtos.Channel.ChannelResponse;
import com.k955.subnet.dtos.User.UserProfileResponse;
import com.k955.subnet.enums.ChannelJoinRequest.Status;

import java.time.Instant;
import java.util.UUID;

public record ChannelJoinRequestResponse(
        UUID id,
        ChannelResponse channel,
        UserProfileResponse user,
        Status status,
        UserProfileResponse reviewer,
        Instant reviewedAt
) {
}
