package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PoolParticipantRepository extends JpaRepository<PoolParticipant, UUID> {

    List<PoolParticipant> findAllByPoolId(UUID poolId);

    Boolean existsByPoolIdAndPlayerId(UUID poolId, UUID userId);

    @Query("Select SUM(p.maxValueToBet) from PoolParticipant p WHERE p.pool.id = :poolId AND p.status = 'CONFIRMED'")
    BigDecimal getConfirmedTotalAmount(@Param("poolId") UUID poolId);

    @Query("Select SUM(p.maxValueToBet) from PoolParticipant p WHERE p.pool.id = :poolId AND p.status IN ('CONFIRMED', 'PENDING')")
    BigDecimal getEstimatedTotalAmount(@Param("poolId") UUID poolId);

}
