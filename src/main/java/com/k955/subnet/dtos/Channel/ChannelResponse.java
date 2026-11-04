package com.k955.subnet.dtos.Channel;

import com.k955.subnet.dtos.User.UserProfileResponse;
import com.k955.subnet.dtos.Workspace.WorkspaceResponse;
import com.k955.subnet.enums.Channel.Type;

import java.time.Instant;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        String name,
        Type type,
        WorkspaceResponse workspace,
        UserProfileResponse createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}
