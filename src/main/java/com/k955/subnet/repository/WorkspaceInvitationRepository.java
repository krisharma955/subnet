package com.k955.subnet.repository;

import com.k955.subnet.entity.WorkspaceInvitation;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitation, UUID> {

    Optional<WorkspaceInvitation> findByInviteTokenHash(@NotBlank(message = "Invitation token is required") String token);

}
