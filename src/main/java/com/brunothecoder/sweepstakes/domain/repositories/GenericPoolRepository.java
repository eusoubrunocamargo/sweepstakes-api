package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.GenericPool;
import com.brunothecoder.sweepstakes.domain.entities.PoolStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface GenericPoolRepository extends JpaRepository<GenericPool, UUID> {
    @Query("SELECT p from GenericPool p WHERE p.status = 'OPEN' AND p.endDate <= :now")
    List<GenericPool> findAllExpiredGenericPools(@Param("now") LocalDateTime now);

    boolean existsByNameAndStatus(String name, PoolStatus poolStatus);

    long countByOrganizer_IdAndStatus(UUID organizerId, PoolStatus status);
}
