package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.GenericPoolParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface GenericPoolParticipantRepository extends JpaRepository<GenericPoolParticipant, UUID> {
    List<GenericPoolParticipant> findAllByGenericPool_Id(UUID genericPoolId);

    @Modifying
    @Query("UPDATE GenericPoolParticipant gp " +
            "SET gp.status = 'EXPIRED' " +
            "WHERE gp.genericPool.id = :poolId " +
            "AND gp.status = 'PENDING'")
    void expirePendingParticipants(@Param("poolId") UUID id);

    @Query("SELECT COALESCE(SUM(gp.genericPool.poolValue), 0) " +
            "FROM GenericPoolParticipant gp " +
            "WHERE gp.genericPool.id = :poolId " +
            "AND gp.status = 'CONFIRMED'")
    BigDecimal getConfirmedTotalAmount(@Param("poolId") UUID poolId);

    interface OptionCountProjection {
        String getOptionLabel();
        Long getCount();
    }
    @Query("SELECT gp.chosenOption.label AS optionLabel," +
            "COUNT(gp) AS count " +
            "FROM GenericPoolParticipant gp " +
            "WHERE gp.genericPool.id = :poolId " +
            "AND gp.status = 'CONFIRMED' " +
            "GROUP BY gp.chosenOption.label")
    List<OptionCountProjection> countConfirmedByOption(@Param("poolId") UUID poolId);
}
