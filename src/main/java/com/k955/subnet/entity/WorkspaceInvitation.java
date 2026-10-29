package com.k955.subnet.entity;

import com.k955.subnet.enums.WorkspaceInvitation.WorkspaceInvitationStatus;
import com.k955.subnet.enums.WorkspaceMember.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "workspace_invitations")
public class WorkspaceInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    Workspace workspace;

    @Column(nullable = false)
    String email; // invitee email

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_id", nullable = false)
    User invitedBy;

    @Column(nullable = false, unique = true)
    String inviteTokenHash; //Unique hash of the secret invitation token

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Role role;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    WorkspaceInvitationStatus status = WorkspaceInvitationStatus.PENDING;

    @Column(nullable = false)
    Instant expiresAt;

    Instant acceptedAt;

    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @Column(nullable = false)
    Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if(createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

}
