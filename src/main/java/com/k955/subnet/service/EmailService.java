package com.k955.subnet.service;

import com.k955.subnet.enums.WorkspaceMember.Role;

import java.time.Instant;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendWorkspaceInvitation(
            String to, String workspaceName, String inviterName,
            Role role, String inviteLink, Instant expiresAt
    );

}
