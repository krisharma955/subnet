package com.k955.subnet.service;

import com.k955.subnet.dtos.Channel.ChannelResponse;
import com.k955.subnet.dtos.Channel.CreateChannelRequest;
import com.k955.subnet.dtos.Channel.UpdateChannelRequest;
import com.k955.subnet.dtos.ChannelJoinRequest.ChannelJoinRequestResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChannelService {

    ChannelResponse createChannel(UUID workspaceId, @Valid CreateChannelRequest createChannelRequest);

    ChannelResponse getChannelById(UUID channelId);

    ChannelResponse updateChannel(UUID channelId, UpdateChannelRequest updateChannelRequest);

    void deleteChannel(UUID channelId);

    Page<ChannelResponse> getDiscoverableChannels(UUID workspaceId, Pageable pageable);

    void joinPublicChannel(UUID channelId);

    void leaveChannel(UUID channelId);

    ChannelJoinRequestResponse requestPrivateChannelAccess(UUID channelId);

    void cancelJoinRequest(UUID channelId);

    ChannelJoinRequestResponse approveRequest(UUID channelId, UUID requestId);

    ChannelJoinRequestResponse rejectRequest(UUID channelId, UUID requestId);

}
