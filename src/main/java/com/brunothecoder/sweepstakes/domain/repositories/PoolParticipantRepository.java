package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PoolParticipantRepository extends JpaRepository<PoolParticipant, UUID> {

    //---> optimized queries

    //load participants with player and pool
    @Query("SELECT pp FROM PoolParticipant pp JOIN FETCH pp.player JOIN FETCH pp.pool WHERE pp.pool.id = :poolId")
    List<PoolParticipant> findAllByPoolIdWithPlayerAndPool(@Param("poolId") UUID poolId);

    //load participants information
    @Query("SELECT pp FROM PoolParticipant pp JOIN FETCH pp.player WHERE pp.pool.id = :poolId")
    List<PoolParticipant> findAllByPoolIdWithPlayer(@Param("poolId") UUID poolId);

    //load confirmed participants
    @Query("SELECT pp FROM PoolParticipant pp JOIN FETCH pp.player WHERE pp.pool.id = :poolId AND pp.status = 'CONFIRMED'")
    List<PoolParticipant> findConfirmedByPoolIdWithPlayer(@Param("poolId") UUID poolId);

    //---> aggregation queries

    //sum confirmed
    @Query("Select SUM(p.maxValueToBet) from PoolParticipant p WHERE p.pool.id = :poolId AND p.status = 'CONFIRMED'")
    BigDecimal getConfirmedTotalAmount(@Param("poolId") UUID poolId);

    //sum potential
    @Query("SELECT SUM(p.maxValueToBet) FROM PoolParticipant p WHERE p.pool.id = :poolId AND p.status IN ('CONFIRMED', 'PENDING')")
    BigDecimal getEstimatedTotalAmount(@Param("poolId") UUID poolId);

    //---> verification queries
    Boolean existsByPoolIdAndPlayerId(UUID poolId, UUID userId);

    //---> deprecated
    @Deprecated
    List<PoolParticipant> findAllByPoolId(UUID poolId);
}
