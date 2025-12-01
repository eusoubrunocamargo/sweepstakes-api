package com.brunothecoder.sweepstakes.api.controllers;

import com.brunothecoder.sweepstakes.api.dto.genericpool_participant.GenericParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.genericpool_participant.GenericParticipantResponseDTO;
import com.brunothecoder.sweepstakes.application.services.GenericPoolParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pools/generic/{poolId}/participants")
public class GenericPoolParticipantController {

    private final GenericPoolParticipantService service;

    public GenericPoolParticipantController(GenericPoolParticipantService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GenericParticipantResponseDTO> joinGenericPool(
            @PathVariable UUID poolId,
            @Valid @RequestBody GenericParticipantRequestDTO request
            ) {
        GenericParticipantResponseDTO response = service.joinGenericPool(
                poolId,
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GenericParticipantResponseDTO>>listParticipants
            (@PathVariable UUID poolId){
        List<GenericParticipantResponseDTO> participants = service.listParticipantsByGenericPoolId(poolId);
        return ResponseEntity.ok(participants);
    }
}
