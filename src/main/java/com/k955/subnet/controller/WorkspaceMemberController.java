package com.k955.subnet.controller;

import com.k955.subnet.dtos.Workspace.WorkspaceMemberResponse;
import com.k955.subnet.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;

    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<Page<WorkspaceMemberResponse>> getWorkspaceMembers(
            @PathVariable UUID workspaceId,
            @PageableDefault(size = 10, sort = "joinedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(workspaceMemberService.getWorkspaceMembers(workspaceId, pageable));
    }

    @DeleteMapping("/{workspaceId}/members/{userId}")
    public ResponseEntity<Void> removeWorkspaceMember(
            @PathVariable UUID workspaceId, @PathVariable UUID userId
    ) {
        workspaceMemberService.removeWorkspaceMember(workspaceId, userId);
        return ResponseEntity.noContent().build();
    }

}
