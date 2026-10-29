package com.k955.subnet.controller;

import com.k955.subnet.dtos.WorkspaceInvitation.WorkspaceInvitationAcceptRequest;
import com.k955.subnet.dtos.WorkspaceInvitation.WorkspaceInvitationRejectRequest;
import com.k955.subnet.dtos.WorkspaceInvitation.WorkspaceInvitationRequest;
import com.k955.subnet.service.WorkspaceInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invitations")
public class WorkspaceInvitationController {

    private final WorkspaceInvitationService workspaceInvitationService;

    @PostMapping("/{workspaceId}")
    public ResponseEntity<Void> inviteUser(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody WorkspaceInvitationRequest workspaceInvitationRequest
    ) {
        workspaceInvitationService.inviteUser(workspaceId, workspaceInvitationRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accept-invite")
    public ResponseEntity<Void> acceptInvite(
            @Valid @RequestBody WorkspaceInvitationAcceptRequest workspaceInvitationAcceptRequest
    ) {
        workspaceInvitationService.acceptInvite(workspaceInvitationAcceptRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reject-invite")
    public ResponseEntity<Void> rejectInvite(
            @Valid @RequestBody WorkspaceInvitationRejectRequest workspaceInvitationAcceptRequest
    ) {
        workspaceInvitationService.rejectInvite(workspaceInvitationAcceptRequest);
        return ResponseEntity.noContent().build();
    }

}
