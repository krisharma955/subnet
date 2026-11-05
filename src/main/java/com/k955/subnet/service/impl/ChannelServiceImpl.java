package com.k955.subnet.service.impl;

import com.k955.subnet.dtos.Channel.ChannelResponse;
import com.k955.subnet.dtos.Channel.CreateChannelRequest;
import com.k955.subnet.dtos.Channel.UpdateChannelRequest;
import com.k955.subnet.dtos.ChannelJoinRequest.ChannelJoinRequestResponse;
import com.k955.subnet.entity.*;
import com.k955.subnet.enums.Channel.Type;
import com.k955.subnet.enums.ChannelJoinRequest.Status;
import com.k955.subnet.enums.WorkspaceMember.Role;
import com.k955.subnet.exception.BadRequestException;
import com.k955.subnet.exception.ErrorCode;
import com.k955.subnet.exception.ResourceNotFoundException;
import com.k955.subnet.mapper.ChannelJoinRequestMapper;
import com.k955.subnet.mapper.ChannelMapper;
import com.k955.subnet.repository.*;
import com.k955.subnet.security.JwtAuthUtil;
import com.k955.subnet.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ChannelMapper channelMapper;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelJoinRequestRepository channelJoinRequestRepository;
    private final ChannelJoinRequestMapper channelJoinRequestMapper;

    @Override
    @Transactional
    public ChannelResponse createChannel(UUID workspaceId, CreateChannelRequest createChannelRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_MEMBER_NOT_FOUND, "Your Membership related to this Workspace was not found."
                ));

        Role role = workspaceMember.getRole();
        if(!role.equals(Role.OWNER) && !role.equals(Role.ADMIN)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Owners & Admins are allowed can create channels");
        }

        boolean exists = channelRepository.existsByWorkspaceIdAndName(workspaceId, createChannelRequest.name());
        if(exists) throw new BadRequestException(ErrorCode.BAD_REQUEST, "A Channel with this name already exists.");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WORKSPACE_NOT_FOUND, "The Workspace Details can't be loaded at the moment. Try Later!"
                ));

        Channel channel = Channel.builder()
                .workspace(workspace)
                .name(createChannelRequest.name())
                .description(createChannelRequest.description())
                .type(createChannelRequest.type())
                .createdBy(user)
                .build();
        Channel saved = channelRepository.save(channel);

        ChannelMember channelMember = ChannelMember.builder()
                .channel(channel)
                .user(user)
                .role(com.k955.subnet.enums.ChannelMember.Role.ADMIN)
                .build();
        channelMemberRepository.save(channelMember);

        return channelMapper.toChannelResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelResponse getChannelById(UUID channelId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        ChannelMember channelMember = channelMemberRepository.findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Your Membership related to this Channel was not found."
                ));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at the moment. Try Later!"
                ));

        return channelMapper.toChannelResponse(channel);
    }

    @Override
    @Transactional
    public ChannelResponse updateChannel(UUID channelId, UpdateChannelRequest updateChannelRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        ChannelMember channelMember = channelMemberRepository.findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Your Membership related to this Channel was not found."
                ));

        com.k955.subnet.enums.ChannelMember.Role role = channelMember.getRole();
        if(!role.equals(com.k955.subnet.enums.ChannelMember.Role.ADMIN)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Admins are allowed to update Channel Details.");
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at the moment. Try Later!"
                ));

        if(updateChannelRequest.name() != null && !updateChannelRequest.name().isBlank()) {
            channel.setName(updateChannelRequest.name());
        }

        if(updateChannelRequest.description() != null && !updateChannelRequest.description().isBlank()) {
            channel.setDescription(updateChannelRequest.description());
        }

        if(updateChannelRequest.type() != null) {
            channel.setType(updateChannelRequest.type());
        }

        Channel saved = channelRepository.save(channel);

        return channelMapper.toChannelResponse(saved);
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        ChannelMember channelMember = channelMemberRepository.findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Your Membership related to this Channel was not found."
                ));

        com.k955.subnet.enums.ChannelMember.Role role = channelMember.getRole();
        if(!role.equals(com.k955.subnet.enums.ChannelMember.Role.ADMIN)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Admins are allowed to delete Channel.");
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at the moment. Try Later!"
                ));

        channelMemberRepository.deleteAll();
        channelRepository.delete(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChannelResponse> getDiscoverableChannels(UUID workspaceId, Pageable pageable) {
        return channelRepository.findAllByWorkspaceId(workspaceId, pageable)
                .map(channelMapper::toChannelResponse);
    }

    @Override
    @Transactional
    public void joinPublicChannel(UUID channelId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at the moment. Try Later!"
                ));

        boolean workspaceMembership = workspaceMemberRepository
                .existsByWorkspaceIdAndUserId(channel.getWorkspace().getId(), userId);
        if(!workspaceMembership) throw new BadRequestException(ErrorCode.BAD_REQUEST, "Your Membership related to this Workspace was not found.");

        if(channel.getType().equals(Type.PRIVATE)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "The Channel is PRIVATE. Request to join the channel.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        ChannelMember newChannelMember = ChannelMember.builder()
                .channel(channel)
                .user(user)
                .role(com.k955.subnet.enums.ChannelMember.Role.MEMBER)
                .build();
        channelMemberRepository.save(newChannelMember);
    }

    @Override
    @Transactional
    public void leaveChannel(UUID channelId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        ChannelMember channelMember = channelMemberRepository.findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Your Membership related to this Channel was not found."
                ));

        channelMemberRepository.delete(channelMember);
    }

    @Override
    @Transactional
    public ChannelJoinRequestResponse requestPrivateChannelAccess(UUID channelId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at the moment. Try Later!"
                ));

        boolean workspaceMembership = workspaceMemberRepository
                .existsByWorkspaceIdAndUserId(channel.getWorkspace().getId(), userId);
        if(!workspaceMembership) throw new BadRequestException(ErrorCode.BAD_REQUEST, "Your Membership related to this Workspace was not found.");

        if(!channel.getType().equals(Type.PRIVATE)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "The Channel is already PUBLIC. You can join it directly");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        ChannelJoinRequest channelJoinRequest = ChannelJoinRequest.builder()
                .channel(channel)
                .user(user)
                .build();
        ChannelJoinRequest saved = channelJoinRequestRepository.save(channelJoinRequest);

        return channelJoinRequestMapper.toChannelJoinRequestResponse(saved);
    }

    @Override
    @Transactional
    public void cancelJoinRequest(UUID channelId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at the moment. Try Later!"
                ));

        boolean workspaceMembership = workspaceMemberRepository
                .existsByWorkspaceIdAndUserId(channel.getWorkspace().getId(), userId);
        if(!workspaceMembership) throw new BadRequestException(ErrorCode.BAD_REQUEST, "Your Membership related to this Workspace was not found.");

        ChannelJoinRequest channelJoinRequest = channelJoinRequestRepository
                .findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_JOIN_REQUEST_NOT_FOUND, "Join Request not found!"
                ));

        channelJoinRequest.setStatus(Status.CANCELLED);
        channelJoinRequestRepository.save(channelJoinRequest);
    }

    @Override
    public ChannelJoinRequestResponse approveRequest(UUID channelId, UUID requestId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        ChannelMember channelMember = channelMemberRepository.findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Your Membership related to this Channel was not found."
                ));

        com.k955.subnet.enums.ChannelMember.Role role = channelMember.getRole();
        if(!role.equals(com.k955.subnet.enums.ChannelMember.Role.ADMIN) && !role.equals(com.k955.subnet.enums.ChannelMember.Role.MODERATOR)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Admins/Moderators can approve requests.");
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at the moment. Try Later!"
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        ChannelJoinRequest channelJoinRequest = channelJoinRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_JOIN_REQUEST_NOT_FOUND, "Join Request not found!"
                ));

        User newMember = userRepository.findById(channelJoinRequest.getUser().getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                ErrorCode.USER_NOT_FOUND, "The requested user's details can't be loaded at the moment."
                        ));

        channelJoinRequest.setStatus(Status.APPROVED);
        channelJoinRequest.setReviewedBy(user);
        channelJoinRequest.setReviewedAt(Instant.now());

        ChannelJoinRequest saved = channelJoinRequestRepository.save(channelJoinRequest);

        ChannelMember newChannelMember = ChannelMember.builder()
                .channel(channel)
                .user(newMember)
                .role(com.k955.subnet.enums.ChannelMember.Role.MEMBER)
                .addedBy(user)
                .build();
        channelMemberRepository.save(newChannelMember);

        return channelJoinRequestMapper.toChannelJoinRequestResponse(saved);
    }

    @Override
    public ChannelJoinRequestResponse rejectRequest(UUID channelId, UUID requestId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        ChannelMember channelMember = channelMemberRepository.findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Your Membership related to this Channel was not found."
                ));

        com.k955.subnet.enums.ChannelMember.Role role = channelMember.getRole();
        if(!role.equals(com.k955.subnet.enums.ChannelMember.Role.ADMIN) && !role.equals(com.k955.subnet.enums.ChannelMember.Role.MODERATOR)) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Admins/Moderators can approve requests.");
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at the moment. Try Later!"
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        ChannelJoinRequest channelJoinRequest = channelJoinRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_JOIN_REQUEST_NOT_FOUND, "Join Request not found!"
                ));

        channelJoinRequest.setStatus(Status.REJECTED);
        channelJoinRequest.setReviewedBy(user);
        channelJoinRequest.setReviewedAt(Instant.now());

        ChannelJoinRequest saved = channelJoinRequestRepository.save(channelJoinRequest);

        return channelJoinRequestMapper.toChannelJoinRequestResponse(saved);
    }

}
