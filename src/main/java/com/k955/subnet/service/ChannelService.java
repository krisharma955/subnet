package com.k955.subnet.service;

import com.k955.subnet.dtos.Channel.ChannelResponse;
import com.k955.subnet.dtos.Channel.CreateChannelRequest;
import com.k955.subnet.dtos.Channel.UpdateChannelRequest;
import jakarta.validation.Valid;

import java.util.UUID;

public interface ChannelService {

    ChannelResponse createChannel(UUID workspaceId, @Valid CreateChannelRequest createChannelRequest);

    ChannelResponse getChannelById(UUID channelId);

    ChannelResponse updateChannel(UUID channelId, UpdateChannelRequest updateChannelRequest);

    void deleteChannel(UUID channelId);

}
