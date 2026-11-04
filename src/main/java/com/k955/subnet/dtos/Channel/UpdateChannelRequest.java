package com.k955.subnet.dtos.Channel;

import com.k955.subnet.enums.Channel.Type;

public record UpdateChannelRequest(
        String name,
        String description,
        Type type
) {
}
