package com.k955.subnet.service.impl;

import com.k955.subnet.dtos.Channel.ChannelResponse;
import com.k955.subnet.dtos.Channel.CreateChannelRequest;
import com.k955.subnet.dtos.Channel.UpdateChannelRequest;
import com.k955.subnet.entity.*;
import com.k955.subnet.enums.Channel.Type;
import com.k955.subnet.enums.WorkspaceMember.Role;
import com.k955.subnet.exception.BadRequestException;
import com.k955.subnet.exception.ErrorCode;
import com.k955.subnet.exception.ResourceNotFoundException;
import com.k955.subnet.mapper.ChannelMapper;
import com.k955.subnet.repository.*;
import com.k955.subnet.security.JwtAuthUtil;
import com.k955.subnet.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ChannelResponse getChannelById(UUID channelId) {
        return null;
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

}
