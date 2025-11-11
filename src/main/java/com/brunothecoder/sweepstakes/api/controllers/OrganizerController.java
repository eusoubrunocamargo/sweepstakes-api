package com.brunothecoder.sweepstakes.api.controllers;

import com.brunothecoder.sweepstakes.api.dto.organizer.OrganizerRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.organizer.OrganizerResponseDTO;
import com.brunothecoder.sweepstakes.api.mappers.OrganizerMapper;
import com.brunothecoder.sweepstakes.application.services.OrganizerService;
import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/organizer")
public class OrganizerController {

    private final OrganizerService service;
    private final OrganizerMapper mapper;

    public OrganizerController(OrganizerService service, OrganizerMapper mapper){
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OrganizerResponseDTO>>list(){
        return ResponseEntity.ok(service
                .list()
                .stream()
                .map(mapper::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<OrganizerResponseDTO>create(@Valid @RequestBody OrganizerRequestDTO request){
        Organizer saved = service.create(mapper.toEntity(request));
        return ResponseEntity.created(URI.create(
                "/v1/organizer" + saved.getId()))
                .body(mapper.toResponse(saved));
    }


}
