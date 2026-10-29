package com.k955.subnet.dtos.WorkspaceInvitation;

import com.k955.subnet.enums.WorkspaceMember.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkspaceInvitationRequest(
        @NotBlank @Email String email,
        @NotNull Role role
) {
}
