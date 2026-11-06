package com.k955.subnet.repository;

import com.k955.subnet.entity.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, UUID> {

    Optional<MessageReaction> findByMessageIdAndUserId(UUID messageId, UUID userId);

    List<MessageReaction> findAllByMessageId(UUID messageId);

    boolean existsByMessageIdAndUserId(UUID messageId, UUID userId);

}
