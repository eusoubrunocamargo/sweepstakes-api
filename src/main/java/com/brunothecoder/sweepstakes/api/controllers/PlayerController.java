package com.brunothecoder.sweepstakes.api.controllers;

import com.brunothecoder.sweepstakes.api.dto.player.PlayerRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.player.PlayerResponseDTO;
import com.brunothecoder.sweepstakes.api.mappers.PlayerMapper;
import com.brunothecoder.sweepstakes.application.services.PlayerService;
import com.brunothecoder.sweepstakes.domain.entities.Player;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/player")
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    public PlayerController(PlayerService playerService, PlayerMapper playerMapper){
        this.playerMapper = playerMapper;
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponseDTO>> list(){
        return ResponseEntity.ok(playerService
                .list()
                .stream()
                .map(playerMapper::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<PlayerResponseDTO>create(@Valid @RequestBody PlayerRequestDTO playerRequestDTO){
        Player saved = playerService.create(playerMapper.toEntity(playerRequestDTO));
        return ResponseEntity.created(URI.create(
                        "/v1/player" + saved.getId()))
                .body(playerMapper.toResponse(saved));
    }

}
