package com.k955.subnet.service;

import com.k955.subnet.dtos.Workspace.CreateWorkspaceRequest;
import com.k955.subnet.dtos.Workspace.WorkspaceResponse;
import com.k955.subnet.dtos.Workspace.WorkspaceUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WorkspaceService {

    WorkspaceResponse createWorkspace(@Valid CreateWorkspaceRequest createWorkspaceRequest);

    WorkspaceResponse getWorkspaceById(UUID workspaceId);

    Page<WorkspaceResponse> getUserWorkspaces(String slug, String search, Pageable pageable);

    WorkspaceResponse updateWorkspaceById(UUID workspaceId, WorkspaceUpdateRequest workspaceUpdateRequest);

    void deleteWorkspaceById(UUID workspaceId);

}
