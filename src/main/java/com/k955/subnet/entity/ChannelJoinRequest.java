package com.k955.subnet.entity;

import com.k955.subnet.enums.ChannelJoinRequest.Status;
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
@Table(
        name = "channel_join_requests",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_channel_id_user_id", columnNames = {"channel_id", "user_id"}
        )
)
public class ChannelJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Status status = Status.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    User reviewedBy;

    @Column(nullable = false, updatable = false)
    Instant requestedAt;

    Instant reviewedAt;

    @PrePersist
    protected void onRequest() {
        if(requestedAt == null) requestedAt = Instant.now();
    }

}
