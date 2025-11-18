package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.GenericPoolParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface GenericPoolParticipantRepository extends JpaRepository<GenericPoolParticipant, UUID> {
    List<GenericPoolParticipant> findAllByGenericPool_Id(UUID genericPoolId);
}
