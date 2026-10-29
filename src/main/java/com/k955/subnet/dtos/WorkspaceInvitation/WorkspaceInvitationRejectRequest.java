package com.k955.subnet.dtos.WorkspaceInvitation;

import jakarta.validation.constraints.NotBlank;

public record WorkspaceInvitationRejectRequest(
        @NotBlank(message = "Invitation token is required")
        String token
) {
}
