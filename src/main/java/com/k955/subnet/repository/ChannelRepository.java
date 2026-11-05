package com.k955.subnet.repository;

import com.k955.subnet.entity.Channel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    boolean existsByWorkspaceIdAndName(UUID workspaceId, @NotBlank String name);

    Page<Channel> findAllByWorkspaceId(UUID workspaceId, Pageable pageable);

}
