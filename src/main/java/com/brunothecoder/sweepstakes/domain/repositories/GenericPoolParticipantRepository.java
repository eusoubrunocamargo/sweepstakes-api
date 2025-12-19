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

    //---> optimized queries

    //detailed list of participants
    @Query("SELECT gp FROM GenericPoolParticipant gp JOIN FETCH gp.player JOIN FETCH gp.genericPool JOIN FETCH gp.chosenOption WHERE gp.genericPool.id = :poolId")
    List<GenericPoolParticipant> findAllByPoolIdWithAllRelations(@Param("poolId") UUID poolId);

    //load participants with their options
    @Query("SELECT gp FROM GenericPoolParticipant gp " +
            "LEFT JOIN FETCH gp.chosenOption " +
            "WHERE gp.genericPool.id = :poolId")
    List<GenericPoolParticipant> findAllWithOptionsByPoolId(@Param("poolId") UUID poolId);

    //load players and option
    @Query("SELECT gp FROM GenericPoolParticipant gp JOIN FETCH gp.player JOIN FETCH gp.chosenOption WHERE gp.genericPool.id = :poolId")
    List<GenericPoolParticipant> findAllByPoolIdWithPlayerAndOption(@Param("poolId") UUID poolId);

    //---> aggregation queries

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

    //---> bulk updates

    @Modifying
    @Query("UPDATE GenericPoolParticipant gp " +
            "SET gp.status = 'EXPIRED' " +
            "WHERE gp.genericPool.id = :poolId " +
            "AND gp.status = 'PENDING'")
    void expirePendingParticipants(@Param("poolId") UUID id);

    //deprecated
    @Deprecated
    List<GenericPoolParticipant> findAllByGenericPool_Id(UUID genericPoolId);

}
