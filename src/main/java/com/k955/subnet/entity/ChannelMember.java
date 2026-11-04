package com.k955.subnet.entity;

import com.k955.subnet.enums.ChannelMember.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "channel_members",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_channel_user", columnNames = {"channel_id", "user_id"}
        )
)
public class ChannelMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_id")
    User addedBy;

    @Column(nullable = false, updatable = false)
    Instant joinedAt;

    @PrePersist
    protected void onJoin() {
        if(joinedAt == null) joinedAt = Instant.now();
    }

}
