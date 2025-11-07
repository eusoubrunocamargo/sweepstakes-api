package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizerRepository extends JpaRepository<Organizer, UUID> {
}
