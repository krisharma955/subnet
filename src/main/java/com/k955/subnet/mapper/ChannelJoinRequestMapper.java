package com.k955.subnet.mapper;

import com.k955.subnet.dtos.ChannelJoinRequest.ChannelJoinRequestResponse;
import com.k955.subnet.entity.ChannelJoinRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChannelJoinRequestMapper {

    ChannelJoinRequestResponse toChannelJoinRequestResponse(ChannelJoinRequest channelJoinRequest);

}
