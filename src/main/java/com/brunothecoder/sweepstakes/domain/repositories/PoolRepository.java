package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.Pool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PoolRepository extends JpaRepository<Pool, UUID> {
}
