package com.k955.subnet.repository;

import com.k955.subnet.entity.ChannelJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelJoinRequestRepository extends JpaRepository<ChannelJoinRequest, UUID> {

    Optional<ChannelJoinRequest> findByChannelIdAndUserId(UUID channelId, UUID userId);

}
