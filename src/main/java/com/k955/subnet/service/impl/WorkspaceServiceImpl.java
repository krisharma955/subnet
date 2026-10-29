package com.k955.subnet.service.impl;

import com.k955.subnet.dtos.Workspace.CreateWorkspaceRequest;
import com.k955.subnet.dtos.Workspace.WorkspaceResponse;
import com.k955.subnet.dtos.Workspace.WorkspaceUpdateRequest;
import com.k955.subnet.entity.User;
import com.k955.subnet.entity.Workspace;
import com.k955.subnet.entity.WorkspaceMember;
import com.k955.subnet.enums.WorkspaceMember.Role;
import com.k955.subnet.exception.BadRequestException;
import com.k955.subnet.exception.ErrorCode;
import com.k955.subnet.exception.ResourceNotFoundException;
import com.k955.subnet.mapper.WorkspaceMapper;
import com.k955.subnet.repository.UserRepository;
import com.k955.subnet.repository.WorkspaceMemberRepository;
import com.k955.subnet.repository.WorkspaceRepository;
import com.k955.subnet.security.JwtAuthUtil;
import com.k955.subnet.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    @Transactional
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest createWorkspaceRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "Your User Details can't be loaded at the moment. Try Later!"
                ));

        Workspace workspace = Workspace.builder()
                .name(createWorkspaceRequest.name())
                .description(createWorkspaceRequest.description())
                .slug(generateSlug(createWorkspaceRequest.name()))
                .createdBy(user)
                .build();
        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .workspace(workspace)
                .user(user)
                .role(Role.OWNER)
                .build();
        workspaceMemberRepository.save(workspaceMember);

        return workspaceMapper.toWorkspaceResponse(savedWorkspace);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspaceById(UUID workspaceId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_MEMBER_NOT_FOUND, "Your Membership related to this Workspace was not found."
                ));

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_NOT_FOUND, "Your Workspace Details can't be loaded at the moment. Try Later!"
                ));
        return workspaceMapper.toWorkspaceResponse(workspace);
    }

    @Override
    public Page<WorkspaceResponse> getUserWorkspaces(String slug, String search, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public WorkspaceResponse updateWorkspaceById(UUID workspaceId, WorkspaceUpdateRequest workspaceUpdateRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_MEMBER_NOT_FOUND, "Your Membership related to this Workspace was not found."
                ));

        Role role = workspaceMember.getRole();
        if(!role.equals(Role.OWNER) && !role.equals(Role.ADMIN)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Owners & Admins are allowed to update Workspace Details.");
        }

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_NOT_FOUND, "Your Workspace Details can't be loaded at the moment. Try Later!"
                ));

        if(workspaceUpdateRequest.name() != null && !workspaceUpdateRequest.name().isBlank()) {
            workspace.setName(workspaceUpdateRequest.name());
        }

        if(workspaceUpdateRequest.description() != null && !workspaceUpdateRequest.description().isBlank()) {
            workspace.setDescription(workspaceUpdateRequest.description());
        }

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        return workspaceMapper.toWorkspaceResponse(savedWorkspace);
    }

    @Override
    @Transactional
    public void deleteWorkspaceById(UUID workspaceId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_MEMBER_NOT_FOUND, "Your Membership related to this Workspace was not found."
                ));

        Role role = workspaceMember.getRole();
        if(!role.equals(Role.OWNER) && !role.equals(Role.ADMIN)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Owners & Admins can delete the Workspace.");
        }

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_NOT_FOUND, "Your Workspace Details can't be loaded at the moment. Try Later!"
                ));

        workspaceMemberRepository.deleteAll();
        workspaceRepository.delete(workspace);
    }

    /// Utility Methods

    private String generateSlug(String name) {
        return name
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

}
