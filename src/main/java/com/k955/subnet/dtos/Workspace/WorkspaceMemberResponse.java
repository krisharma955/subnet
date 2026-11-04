package com.k955.subnet.dtos.Workspace;

import com.k955.subnet.dtos.User.UserProfileResponse;
import com.k955.subnet.enums.WorkspaceMember.Role;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceMemberResponse(
        UUID id,
        Role role,
        WorkspaceResponse workspace,
        UserProfileResponse user,
        Instant joinedAt
) {
}
