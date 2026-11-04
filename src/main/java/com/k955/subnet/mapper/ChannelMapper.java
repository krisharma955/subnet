package com.k955.subnet.mapper;

import com.k955.subnet.dtos.Channel.ChannelResponse;
import com.k955.subnet.entity.Channel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

    ChannelResponse toChannelResponse(Channel channel);

}
