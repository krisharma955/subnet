package com.k955.subnet.service.impl;

import com.k955.subnet.Util.InvitationTokenUtil;
import com.k955.subnet.dtos.WorkspaceInvitation.WorkspaceInvitationAcceptRequest;
import com.k955.subnet.dtos.WorkspaceInvitation.WorkspaceInvitationRejectRequest;
import com.k955.subnet.dtos.WorkspaceInvitation.WorkspaceInvitationRequest;
import com.k955.subnet.entity.User;
import com.k955.subnet.entity.Workspace;
import com.k955.subnet.entity.WorkspaceInvitation;
import com.k955.subnet.entity.WorkspaceMember;
import com.k955.subnet.enums.WorkspaceInvitation.WorkspaceInvitationStatus;
import com.k955.subnet.enums.WorkspaceMember.Role;
import com.k955.subnet.exception.BadRequestException;
import com.k955.subnet.exception.ErrorCode;
import com.k955.subnet.exception.ResourceNotFoundException;
import com.k955.subnet.repository.UserRepository;
import com.k955.subnet.repository.WorkspaceInvitationRepository;
import com.k955.subnet.repository.WorkspaceMemberRepository;
import com.k955.subnet.repository.WorkspaceRepository;
import com.k955.subnet.security.JwtAuthUtil;
import com.k955.subnet.service.EmailService;
import com.k955.subnet.service.WorkspaceInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceInvitationServiceImpl implements WorkspaceInvitationService {

    private final WorkspaceInvitationRepository workspaceInvitationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final InvitationTokenUtil invitationTokenUtil;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    @Transactional
    public void inviteUser(UUID workspaceId, WorkspaceInvitationRequest workspaceInvitationRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_MEMBER_NOT_FOUND, "Your Membership related to this Workspace was not found."
                ));

        Role role = workspaceMember.getRole();
        if(!role.equals(Role.OWNER) && !role.equals(Role.ADMIN)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Owners & Admins are allowed to invite Users.");
        }

        boolean alreadyMember = workspaceMemberRepository.existsByWorkspaceIdAndUserEmail(workspaceId, workspaceInvitationRequest.email());
        if(alreadyMember) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "User is already a member of this Workspace");
        }

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_NOT_FOUND, "Your Workspace Details can't be loaded at the moment. Try Later!"
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "Your User Details can't be loaded at the moment. Try Later!"
                ));

        Instant expiresAt = Instant.now().plus(24, ChronoUnit.HOURS);

        String inviteRawToken = invitationTokenUtil.generateToken();
        String inviteTokenHash = invitationTokenUtil.hashToken(inviteRawToken);

        WorkspaceInvitation workspaceInvitation = WorkspaceInvitation.builder()
                .workspace(workspace)
                .email(workspaceInvitationRequest.email())
                .invitedBy(user)
                .inviteTokenHash(inviteTokenHash)
                .role(workspaceInvitationRequest.role())
                .expiresAt(expiresAt)
                .build();

        WorkspaceInvitation savedWorkspaceInvitation = workspaceInvitationRepository.save(workspaceInvitation);

        String inviteLink = frontendUrl + "/accept?token=" + inviteRawToken;

        emailService.sendWorkspaceInvitation(
                workspaceInvitationRequest.email(),
                workspace.getName(),
                user.getName(),
                workspaceInvitationRequest.role(),
                inviteLink,
                savedWorkspaceInvitation.getExpiresAt()
        );
    }

    @Override
    @Transactional
    public void acceptInvite(WorkspaceInvitationAcceptRequest workspaceInvitationAcceptRequest) {
        String tokenHash = invitationTokenUtil.hashToken(workspaceInvitationAcceptRequest.token());

        WorkspaceInvitation workspaceInvitation = workspaceInvitationRepository
                .findByInviteTokenHash(workspaceInvitationAcceptRequest.token())
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.INVALID_INVITATION,
                        "This invitation is invalid or no longer available."
                ));

        if(workspaceInvitation.getStatus() != WorkspaceInvitationStatus.PENDING) {
            throw new BadRequestException(
                    ErrorCode.INVALID_INVITATION,
                    "This invitation is no longer pending."
            );
        }

        if(workspaceInvitation.getExpiresAt().isAfter(Instant.now())) {
            throw new BadRequestException(ErrorCode.INVITATION_EXPIRED, "This invitation has expired.");
        }

        UUID userId = jwtAuthUtil.getCurrentUserId();
        UUID workspaceId = workspaceInvitation.getWorkspace().getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "Your User Details can't be loaded at the moment. Try Later!"
                ));

        boolean alreadyMember = workspaceMemberRepository.existsByWorkspaceIdAndUserEmail(workspaceId, user.getEmail());
        if(alreadyMember) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "You are already a member of this Workspace");
        }

        workspaceInvitation.setStatus(WorkspaceInvitationStatus.ACCEPTED);
        workspaceInvitation.setAcceptedAt(Instant.now());
        workspaceInvitationRepository.save(workspaceInvitation);
    }

    @Override
    @Transactional
    public void rejectInvite(WorkspaceInvitationRejectRequest workspaceInvitationRejectRequest) {
        String tokenHash = invitationTokenUtil.hashToken(workspaceInvitationRejectRequest.token());

        WorkspaceInvitation workspaceInvitation = workspaceInvitationRepository
                .findByInviteTokenHash(workspaceInvitationRejectRequest.token())
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.INVALID_INVITATION,
                        "This invitation is invalid or no longer available."
                ));

        if(workspaceInvitation.getStatus() != WorkspaceInvitationStatus.PENDING) {
            throw new BadRequestException(
                    ErrorCode.INVALID_INVITATION,
                    "This invitation is no longer pending."
            );
        }

        if(workspaceInvitation.getExpiresAt().isAfter(Instant.now())) {
            throw new BadRequestException(ErrorCode.INVITATION_EXPIRED, "This invitation has expired.");
        }

        UUID userId = jwtAuthUtil.getCurrentUserId();
        UUID workspaceId = workspaceInvitation.getWorkspace().getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "Your User Details can't be loaded at the moment. Try Later!"
                ));

        boolean alreadyMember = workspaceMemberRepository.existsByWorkspaceIdAndUserEmail(workspaceId, user.getEmail());
        if(alreadyMember) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "You are already a member of this Workspace");
        }

        workspaceInvitation.setStatus(WorkspaceInvitationStatus.REJECTED);
        workspaceInvitationRepository.save(workspaceInvitation);
    }

}
