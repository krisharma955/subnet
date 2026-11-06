package com.k955.subnet.controller;

import com.k955.subnet.dtos.MessageReaction.MessageReactionRequest;
import com.k955.subnet.dtos.MessageReaction.MessageReactionResponse;
import com.k955.subnet.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels/messages")
public class MessageReactionController {

    private final MessageService messageService;

    @PutMapping("/{messageId}/reaction")
    public ResponseEntity<MessageReactionResponse> addMessageReaction(
            @PathVariable UUID messageId, @Valid @RequestBody MessageReactionRequest messageReactionRequest
    ) {
        return ResponseEntity.ok(messageService.addMessageReaction(messageId, messageReactionRequest));
    }

    @DeleteMapping("/{messageId}/reaction")
    public ResponseEntity<Void> removeMessageReaction(@PathVariable UUID messageId) {
        messageService.removeMessageReaction(messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{messageId}/reactions")
    public ResponseEntity<List<MessageReactionResponse>> getMessageReactions(@PathVariable UUID messageId) {
        return ResponseEntity.ok(messageService.getMessageReactions(messageId));
    }

}
