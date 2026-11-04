package com.k955.subnet.service;

import com.k955.subnet.dtos.Workspace.WorkspaceMemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WorkspaceMemberService {

    Page<WorkspaceMemberResponse> getWorkspaceMembers(UUID workspaceId, Pageable pageable);

    void removeWorkspaceMember(UUID workspaceId, UUID userId);

}
