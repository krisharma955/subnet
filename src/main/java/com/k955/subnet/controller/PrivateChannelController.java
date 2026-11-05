package com.k955.subnet.controller;

import com.k955.subnet.dtos.ChannelJoinRequest.ChannelJoinRequestResponse;
import com.k955.subnet.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces")
public class PrivateChannelController {

    private final ChannelService channelService;

    @PostMapping("/channels/{channelId}/join-request")
    public ResponseEntity<ChannelJoinRequestResponse> requestPrivateChannelAccess(@PathVariable UUID channelId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.requestPrivateChannelAccess(channelId));
    }

    @DeleteMapping("/channels/{channelId}/join-request")
    public ResponseEntity<Void> cancelJoinRequest(@PathVariable UUID channelId) {
        channelService.cancelJoinRequest(channelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/channels/{channelId}/join-request/{requestId}/approve")
    public ResponseEntity<ChannelJoinRequestResponse> approveRequest(
            @PathVariable UUID channelId, @PathVariable UUID requestId
    ) {
        return ResponseEntity.ok(channelService.approveRequest(channelId, requestId));
    }

    @PatchMapping("/channels/{channelId}/join-request/{requestId}/reject")
    public ResponseEntity<ChannelJoinRequestResponse> rejectRequest(
            @PathVariable UUID channelId, @PathVariable UUID requestId
    ) {
        return ResponseEntity.ok(channelService.rejectRequest(channelId, requestId));
    }

}
