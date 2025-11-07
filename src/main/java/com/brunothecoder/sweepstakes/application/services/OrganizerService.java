package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import com.brunothecoder.sweepstakes.domain.repositories.OrganizerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizerService {

    private final OrganizerRepository repository;

    public OrganizerService(OrganizerRepository repository){
        this.repository = repository;
    }

    public Organizer create(Organizer organizer){
        return repository.save(organizer);
    }

    public List<Organizer> list(){
        return repository.findAll();
    }

}
