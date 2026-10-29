package com.k955.subnet.dtos.Workspace;

import jakarta.validation.constraints.NotBlank;

public record CreateWorkspaceRequest(
        @NotBlank String name,
        String description
) {
}
