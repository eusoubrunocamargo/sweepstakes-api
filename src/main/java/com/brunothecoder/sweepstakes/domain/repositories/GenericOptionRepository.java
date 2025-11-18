package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.GenericOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GenericOptionRepository extends JpaRepository<GenericOption, UUID> {
    List<GenericOption> findAllByPoolGenericId(UUID id);
}
