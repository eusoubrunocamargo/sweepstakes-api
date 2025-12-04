package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.PoolStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PoolRepository extends JpaRepository<Pool, UUID> {

    @Query("SELECT p from Pool p WHERE p.status = 'OPEN' AND p.endDate <= :now")
    List<Pool> findAllExpiredPools(@Param("now") LocalDateTime now);

    boolean existsByNameAndStatusAndOrganizer_Id(
            String name,
            PoolStatus status,
            UUID organizerId
    );
}
