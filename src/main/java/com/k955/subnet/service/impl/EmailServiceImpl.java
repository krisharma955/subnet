package com.k955.subnet.service.impl;

import com.k955.subnet.enums.WorkspaceMember.Role;
import com.k955.subnet.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @Override
    public void sendWorkspaceInvitation(
            String to, String workspaceName, String inviterName,
            Role role, String inviteLink, Instant expiresAt
    ) {
        String subject = "You're invited to join " + workspaceName
                + " on SubNet";

        String body = """
            Hi,

            %s has invited you to join the "%s" workspace on SubNet.

            Your assigned role will be: %s

            Accept your invitation using the link below:

            %s

            This invitation expires at: %s UTC

            If you weren't expecting this invitation, you can ignore
            this email.

            Thanks,
            The SubNet Team
            """.formatted(
                inviterName,
                workspaceName,
                role.name(),
                inviteLink,
                expiresAt
        );

        sendEmail(to, subject, body);
    }

}
