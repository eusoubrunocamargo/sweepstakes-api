package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantResponseDTO;
import com.brunothecoder.sweepstakes.api.mappers.PoolParticipantMapper;
import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import com.brunothecoder.sweepstakes.domain.entities.Player;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import com.brunothecoder.sweepstakes.domain.repositories.OrganizerRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PlayerRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PoolParticipantService {

    private final PoolParticipantRepository poolParticipantRepository;
    private final PlayerRepository playerRepository;
    private final PoolRepository poolRepository;
    private final OrganizerRepository organizerRepository;
    private final PoolParticipantMapper poolParticipantMapper;

    public PoolParticipantService(
            PoolParticipantRepository poolParticipantRepository,
            PlayerRepository playerRepository,
            PoolRepository poolRepository,
            OrganizerRepository organizerRepository,
            PoolParticipantMapper poolParticipantMapper
    ){
        this.poolParticipantRepository = poolParticipantRepository;
        this.playerRepository = playerRepository;
        this.poolRepository = poolRepository;
        this.organizerRepository = organizerRepository;
        this.poolParticipantMapper = poolParticipantMapper;
    }

    public PoolParticipantResponseDTO joinPool(
            UUID poolId,
            PoolParticipantRequestDTO poolParticipantRequestDTO){

        //check if organizer exists
        Organizer organizer = organizerRepository.findById(poolParticipantRequestDTO.organizerId())
                .orElseThrow(()-> new EntityNotFoundException("Organizer not found!"));
        //check if informed keyword matches organizer keyword
        if(!organizer.getKeyword().equals(poolParticipantRequestDTO.keyword())){
            throw new IllegalArgumentException("Invalid keyword for this organizer.");
        }
        //check if pool exists
        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(()-> new EntityNotFoundException("Pool not found!"));
        //check if player exists
        Player player = playerRepository.findById(poolParticipantRequestDTO.playerId())
                .orElseThrow(()-> new EntityNotFoundException("Player not found!"));

        PoolParticipant poolParticipant = poolParticipantMapper.toEntity(poolParticipantRequestDTO, player, pool);
        poolParticipant.setJoinedAt(LocalDateTime.now());
        poolParticipantRepository.save(poolParticipant);
        return poolParticipantMapper.toResponse(poolParticipant);
    }

    public List<PoolParticipantResponseDTO> listParticipantsByPool(UUID poolId){
        return poolParticipantRepository.findAllByPoolId(poolId)
                .stream()
                .map(poolParticipantMapper::toResponse)
                .toList();
    }
}
