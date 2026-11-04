package com.k955.subnet.controller;

import com.k955.subnet.dtos.Channel.ChannelResponse;
import com.k955.subnet.dtos.Channel.CreateChannelRequest;
import com.k955.subnet.dtos.Channel.UpdateChannelRequest;
import com.k955.subnet.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/{workspaceId}/channels")
    public ResponseEntity<ChannelResponse> createChannel(
            @PathVariable UUID workspaceId, @Valid @RequestBody CreateChannelRequest createChannelRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createChannel(workspaceId, createChannelRequest));
    }

    @GetMapping("/channels/{channelId}")
    public ResponseEntity<ChannelResponse> getChannelById(@PathVariable UUID channelId) {
        return ResponseEntity.ok(channelService.getChannelById(channelId));
    }

    @PatchMapping("/channels/{channelId}")
    public ResponseEntity<ChannelResponse> updateChannel(
            @PathVariable UUID channelId, @RequestBody UpdateChannelRequest updateChannelRequest
    ) {
        return ResponseEntity.ok(channelService.updateChannel(channelId, updateChannelRequest));
    }

    @DeleteMapping("/channels/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }

}
