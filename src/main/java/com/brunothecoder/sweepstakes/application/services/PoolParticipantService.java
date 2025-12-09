package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantResponseDTO;
import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import com.brunothecoder.sweepstakes.api.mappers.PoolParticipantMapper;
import com.brunothecoder.sweepstakes.domain.entities.*;
import com.brunothecoder.sweepstakes.domain.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PoolParticipantService {

    private final PoolParticipantRepository poolParticipantRepository;
    private final UserRepository userRepository;
    private final PoolRepository poolRepository;
    private final PoolParticipantMapper poolParticipantMapper;
//    private final PoolService poolService;

    public PoolParticipantService(
            PoolParticipantRepository poolParticipantRepository,
            UserRepository userRepository,
            PoolRepository poolRepository,
            PoolParticipantMapper poolParticipantMapper,
            PoolService poolService

    ){
        this.poolParticipantRepository = poolParticipantRepository;
        this.userRepository = userRepository;
        this.poolRepository = poolRepository;
        this.poolParticipantMapper = poolParticipantMapper;
//        this.poolService = poolService;
    }

    @Transactional
    public PoolParticipantResponseDTO joinPool(
            UUID poolId,
            PoolParticipantRequestDTO poolParticipantRequestDTO){

        //check if pool exists
        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(()-> new EntityNotFoundException((ErrorMessages.POOL_NOT_FOUND)));

        //check if informed keyword matches pool keyword
        pool.validateKeyword(poolParticipantRequestDTO.keyword());

        //check if user_maxValueToBet is within Pool range
        pool.validateBetValue(poolParticipantRequestDTO.maxValueToBet());

        //check if player exists and is validated
        User player = userRepository.findById(poolParticipantRequestDTO.userId())
                .orElseThrow(()-> new EntityNotFoundException(ErrorMessages.USER_NOT_FOUND));

        //create participant
        PoolParticipant poolParticipant = poolParticipantMapper.toEntity(poolParticipantRequestDTO, player, pool);

        poolParticipantRepository.save(poolParticipant);

        return poolParticipantMapper.toResponse(poolParticipant);
    }

    public List<PoolParticipantResponseDTO> listParticipantsByPool(UUID poolId){
        return poolParticipantRepository.findAllByPoolId(poolId)
                .stream()
                .map(poolParticipantMapper::toResponse)
                .toList();
    }

    @Transactional
    public void confirmParticipantPayment(UUID participantId){

        PoolParticipant participant = poolParticipantRepository.findById(participantId)
                .orElseThrow(()-> new EntityNotFoundException("Pool Participant not found."));

        participant.confirmPayment();

        poolParticipantRepository.save(participant);
    }
}
