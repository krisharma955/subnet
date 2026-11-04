package com.k955.subnet.service.impl;

import com.k955.subnet.dtos.Workspace.WorkspaceMemberResponse;
import com.k955.subnet.entity.WorkspaceMember;
import com.k955.subnet.enums.WorkspaceMember.Role;
import com.k955.subnet.exception.BadRequestException;
import com.k955.subnet.exception.ErrorCode;
import com.k955.subnet.exception.ResourceNotFoundException;
import com.k955.subnet.mapper.WorkspaceMemberMapper;
import com.k955.subnet.repository.UserRepository;
import com.k955.subnet.repository.WorkspaceMemberRepository;
import com.k955.subnet.repository.WorkspaceRepository;
import com.k955.subnet.security.JwtAuthUtil;
import com.k955.subnet.service.WorkspaceMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {

    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final WorkspaceMemberMapper workspaceMemberMapper;

    @Override
    @Transactional
    public Page<WorkspaceMemberResponse> getWorkspaceMembers(UUID workspaceId, Pageable pageable) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        WorkspaceMember currentUserMembership = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_MEMBER_NOT_FOUND, "Your Membership related to this Workspace was not found."
                ));

        Page<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findAllByWorkspaceId(workspaceId, pageable);

        return workspaceMembers.map(workspaceMemberMapper::toWorkspaceMemberResponse);
    }

    @Override
    @Transactional
    public void removeWorkspaceMember(UUID workspaceId, UUID userId) {
        UUID currentUserId = jwtAuthUtil.getCurrentUserId();

        WorkspaceMember currentUserMembership = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_MEMBER_NOT_FOUND, "Your Membership related to this Workspace was not found."
                ));

        Role role = currentUserMembership.getRole();
        if(!role.equals(Role.OWNER) && !role.equals(Role.ADMIN)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Owners & Admins are allowed to remove members.");
        }

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_MEMBER_NOT_FOUND, "Your Membership related to this Workspace was not found."
                ));

        workspaceMemberRepository.delete(workspaceMember);
    }

}
