package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PoolParticipantRepository extends JpaRepository<PoolParticipant, UUID> {
    List<PoolParticipant> findAllByPoolId(UUID poolId);
    Boolean existsByPoolIdAndPlayerId(UUID poolId, UUID userId);
}
