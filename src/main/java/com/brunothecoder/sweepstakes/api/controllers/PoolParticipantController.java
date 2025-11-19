package com.brunothecoder.sweepstakes.api.controllers;

import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantResponseDTO;
import com.brunothecoder.sweepstakes.application.services.PoolParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pools/{poolId}/participants")
public class PoolParticipantController {

    private final PoolParticipantService poolParticipantService;

   public PoolParticipantController(PoolParticipantService poolParticipantService){
       this.poolParticipantService = poolParticipantService;
   }

   @PostMapping
   public ResponseEntity<PoolParticipantResponseDTO> joinPool(
           @PathVariable UUID poolId,
           @RequestBody @Valid PoolParticipantRequestDTO poolParticipantRequestDTO
   ){
       PoolParticipantResponseDTO poolParticipantResponseDTO =
               poolParticipantService.joinPool(poolId, poolParticipantRequestDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(poolParticipantResponseDTO);
   }

   @GetMapping
   public ResponseEntity<List<PoolParticipantResponseDTO>>
           listParticipants(@PathVariable UUID poolId){
       List<PoolParticipantResponseDTO> participants = poolParticipantService.listParticipantsByPool(poolId);
       return ResponseEntity.ok(participants);
   }

   @PatchMapping("/{participantId}/confirm")
    public ResponseEntity<Void>
   confirmeParticipantPayment(@PathVariable UUID participantId){
       poolParticipantService.confirmParticipantPayment(participantId);
       return ResponseEntity.ok().build();
   }
}
