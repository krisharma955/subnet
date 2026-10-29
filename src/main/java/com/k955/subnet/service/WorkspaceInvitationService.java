package com.k955.subnet.service;

import com.k955.subnet.dtos.WorkspaceInvitation.WorkspaceInvitationAcceptRequest;
import com.k955.subnet.dtos.WorkspaceInvitation.WorkspaceInvitationRejectRequest;
import com.k955.subnet.dtos.WorkspaceInvitation.WorkspaceInvitationRequest;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface WorkspaceInvitationService {

    void inviteUser(UUID workspaceId, @Valid WorkspaceInvitationRequest workspaceInvitationRequest);

    void acceptInvite(@Valid WorkspaceInvitationAcceptRequest workspaceInvitationAcceptRequest);

    void rejectInvite(@Valid WorkspaceInvitationRejectRequest workspaceInvitationRejectRequest);

}
