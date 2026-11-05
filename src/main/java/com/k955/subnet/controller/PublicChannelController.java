package com.k955.subnet.controller;

import com.k955.subnet.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class PublicChannelController {

    private final ChannelService channelService;

    @PostMapping("/channels/{channelId}/join")
    public ResponseEntity<Void> joinPublicChannel(@PathVariable UUID channelId) {
        channelService.joinPublicChannel(channelId);
        return ResponseEntity.noContent().build();
    }

}
