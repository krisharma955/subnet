package com.k955.subnet.mapper;

import com.k955.subnet.dtos.MessageReaction.MessageReactionResponse;
import com.k955.subnet.entity.MessageReaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageReactionMapper {

    MessageReactionResponse toMessageReactionResponse(MessageReaction messageReaction);

    List<MessageReactionResponse> toListOfMessageReactionResponse(List<MessageReaction> messageReactions);

}
