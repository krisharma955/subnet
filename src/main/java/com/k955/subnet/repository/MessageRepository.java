package com.k955.subnet.repository;

import com.k955.subnet.dtos.Message.MessageResponse;
import com.k955.subnet.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

}
