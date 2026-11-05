package com.k955.subnet.service;

import com.k955.subnet.dtos.Message.MessageRequest;
import com.k955.subnet.dtos.Message.MessageResponse;
import com.k955.subnet.dtos.Message.UpdateMessageRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageService {

    MessageResponse sendMessage(UUID channelId, @Valid MessageRequest messageRequest);

    Page<MessageResponse> getChannelMessages(UUID channelId, Pageable pageable);

    MessageResponse getMessageById(UUID messageId);

    MessageResponse updateMessageById(UUID messageId, UpdateMessageRequest updateMessageRequest);

    void deleteMessageById(UUID messageId);

}
