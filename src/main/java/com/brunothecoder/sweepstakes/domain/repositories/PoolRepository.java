package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.PoolStatus;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PoolRepository extends JpaRepository<Pool, UUID> {

    //*** optimized queries ***//

    //all pools loaded with organizers
    @Query("SELECT p FROM Pool p JOIN FETCH p.organizer")
    List<Pool> findAllWithOrganizer();

    //specific pool with organizer
    @Query("SELECT p FROM Pool p JOIN FETCH p.organizer WHERE p.id = :poolId")
    Optional<Pool> findByIdWithOrganizer(@Param("poolId") UUID poolId);

    //scheduled pool closing
    @Query("SELECT p FROM Pool p JOIN FETCH p.organizer WHERE p.status = 'OPEN' AND p.endDate <= :now")
    List<Pool> findAllExpiredPoolWithOrganizer(@Param("now")LocalDateTime now);

    //*** verification queries ***//

    boolean existsByNameAndStatusAndOrganizer_Id(
            String name,
            PoolStatus status,
            UUID organizerId
    );

    //deprecated
    @Deprecated
    @Query("SELECT p from Pool p WHERE p.status = 'OPEN' AND p.endDate <= :now")
    List<Pool> findAllExpiredPools(@Param("now") LocalDateTime now);

}
