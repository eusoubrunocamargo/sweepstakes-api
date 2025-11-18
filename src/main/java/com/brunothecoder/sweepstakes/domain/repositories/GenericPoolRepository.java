package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.GenericPool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GenericPoolRepository extends JpaRepository<GenericPool, UUID> {
}
