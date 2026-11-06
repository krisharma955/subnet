package com.k955.subnet.controller;

import com.k955.subnet.dtos.Message.MessageRequest;
import com.k955.subnet.dtos.Message.MessageResponse;
import com.k955.subnet.dtos.Message.UpdateMessageRequest;
import com.k955.subnet.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/channels/{channelId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID channelId, @Valid @RequestBody MessageRequest messageRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.sendMessage(channelId, messageRequest));
    }

    @GetMapping("/channels/{channelId}/messages")
    public ResponseEntity<Page<MessageResponse>> getChannelMessages(
            @PathVariable UUID channelId,
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return ResponseEntity.ok(messageService.getChannelMessages(channelId, pageable));
    }

    @GetMapping("/channels/messages/{messageId}")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable UUID messageId) {
        return ResponseEntity.ok(messageService.getMessageById(messageId));
    }

    @PatchMapping("/channels/messages/{messageId}")
    public ResponseEntity<MessageResponse> updateMessageById(
            @PathVariable UUID messageId, @RequestBody UpdateMessageRequest updateMessageRequest
    ) {
        return ResponseEntity.ok(messageService.updateMessageById(messageId, updateMessageRequest));
    }

    @DeleteMapping("/channels/messages/{messageId}")
    public ResponseEntity<Void> deleteMessageById(@PathVariable UUID messageId) {
        messageService.deleteMessageById(messageId);
        return ResponseEntity.noContent().build();
    }

    /// Threaded Messages

    @PostMapping("/channels/messages/{messageId}/replies")
    public ResponseEntity<MessageResponse> replyToMessage(
            @PathVariable UUID messageId, @Valid @RequestBody MessageRequest messageRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.replyToMessage(messageId, messageRequest));
    }

    @GetMapping("/channels/messages/{messageId}/replies")
    public ResponseEntity<Page<MessageResponse>> getReplies(
            @PathVariable UUID messageId,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(messageService.getReplies(messageId, pageable));
    }

}
