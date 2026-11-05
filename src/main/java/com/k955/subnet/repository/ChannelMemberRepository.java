package com.k955.subnet.repository;

import com.k955.subnet.entity.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelMemberRepository extends JpaRepository<ChannelMember, UUID> {

    Optional<ChannelMember> findByChannelIdAndUserId(UUID channelId, UUID userId);

    boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);

}
