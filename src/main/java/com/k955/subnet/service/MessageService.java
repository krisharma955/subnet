package com.k955.subnet.service;

import com.k955.subnet.dtos.Message.MessageRequest;
import com.k955.subnet.dtos.Message.MessageResponse;
import com.k955.subnet.dtos.Message.UpdateMessageRequest;
import com.k955.subnet.dtos.MessageReaction.MessageReactionRequest;
import com.k955.subnet.dtos.MessageReaction.MessageReactionResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponse sendMessage(UUID channelId, @Valid MessageRequest messageRequest);

    Page<MessageResponse> getChannelMessages(UUID channelId, Pageable pageable);

    MessageResponse getMessageById(UUID messageId);

    MessageResponse updateMessageById(UUID messageId, UpdateMessageRequest updateMessageRequest);

    void deleteMessageById(UUID messageId);

    MessageResponse replyToMessage(UUID messageId, @Valid MessageRequest messageRequest);

    Page<MessageResponse> getReplies(UUID messageId, Pageable pageable);

    MessageReactionResponse addMessageReaction(UUID messageId, @Valid MessageReactionRequest messageReactionRequest);

    void removeMessageReaction(UUID messageId);

    List<MessageReactionResponse> getMessageReactions(UUID messageId);
}
