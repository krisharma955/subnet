package com.k955.subnet.mapper;

import com.k955.subnet.dtos.MessageReaction.MessageReactionResponse;
import com.k955.subnet.entity.MessageReaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageReactionMapper {

    MessageReactionResponse toMessageReactionResponse(MessageReaction messageReaction);

}
