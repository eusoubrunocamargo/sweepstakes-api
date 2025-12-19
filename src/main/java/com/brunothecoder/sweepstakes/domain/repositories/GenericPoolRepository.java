package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.GenericPool;
import com.brunothecoder.sweepstakes.domain.entities.PoolStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenericPoolRepository extends JpaRepository<GenericPool, UUID> {

    //return expired pools for closing scheduled job
    @Query("SELECT gp FROM GenericPool gp JOIN FETCH gp.organizer WHERE gp.status = 'OPEN' AND gp.endDate <=:now")
    List<GenericPool> findAllExpiredWithOrganizer(@Param("now") LocalDateTime now);


    //return specific generic pool with options and organizer
    @Query("SELECT gp FROM GenericPool gp LEFT JOIN FETCH gp.options JOIN fetch gp.organizer WHERE gp.id = :poolId")
    Optional<GenericPool> findByIdWithOptionsAndOrganizer(@Param("poolId") UUID poolId);

    //Verification queries
    boolean existsByNameAndStatusAndOrganizer_Id(
            String name,
            PoolStatus poolStatus,
            UUID organizerId
            );

    long countByOrganizer_IdAndStatus(UUID organizerId, PoolStatus status);

    @Query("SELECT DISTINCT gp FROM GenericPool gp " +
           "LEFT JOIN FETCH gp.options " +
           "JOIN FETCH gp.organizer")
    List<GenericPool> findAllWithOptionsAndOrganizer();

    //deprecated
    @Deprecated
    @Query("SELECT p from GenericPool p WHERE p.status = 'OPEN' AND p.endDate <= :now")
    List<GenericPool> findAllExpiredGenericPools(@Param("now") LocalDateTime now);
}
