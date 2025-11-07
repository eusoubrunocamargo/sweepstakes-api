package com.brunothecoder.sweepstakes.api.controllers;

import com.brunothecoder.sweepstakes.api.dto.organizer.CreateOrganizerRequest;
import com.brunothecoder.sweepstakes.api.dto.organizer.OrganizerResponse;
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
    public ResponseEntity<List<OrganizerResponse>>list(){
        return ResponseEntity.ok(service
                .list()
                .stream()
                .map(mapper::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<OrganizerResponse>create(@Valid @RequestBody CreateOrganizerRequest request){
        Organizer saved = service.create(mapper.toEntity(request));
        return ResponseEntity.created(URI.create(
                "/v1/organizer" + saved.getId()))
                .body(mapper.toResponse(saved));
    }


}
