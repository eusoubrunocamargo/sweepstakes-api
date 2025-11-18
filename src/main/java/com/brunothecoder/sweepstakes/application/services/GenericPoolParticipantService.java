package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.genericpool_participant.GenericParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.genericpool_participant.GenericParticipantResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolRequestDTO;
import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import com.brunothecoder.sweepstakes.api.mappers.GenericParticipantMapper;
import com.brunothecoder.sweepstakes.domain.entities.GenericOption;
import com.brunothecoder.sweepstakes.domain.entities.GenericPool;
import com.brunothecoder.sweepstakes.domain.entities.GenericPoolParticipant;
import com.brunothecoder.sweepstakes.domain.entities.User;
import com.brunothecoder.sweepstakes.domain.repositories.GenericOptionRepository;
import com.brunothecoder.sweepstakes.domain.repositories.GenericPoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.GenericPoolRepository;
import com.brunothecoder.sweepstakes.domain.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GenericPoolParticipantService {

    private final GenericPoolParticipantRepository genericPoolParticipantRepository;
    private final UserRepository userRepository;
    private final GenericPoolRepository genericPoolRepository;
    private final GenericOptionRepository genericOptionRepository;
    private final GenericParticipantMapper genericParticipantMapper;

    public GenericPoolParticipantService(
            GenericPoolParticipantRepository genericPoolParticipantRepository,
            UserRepository userRepository,
            GenericPoolRepository genericPoolRepository,
            GenericOptionRepository genericOptionRepository,
            GenericParticipantMapper genericParticipantMapper
    ){
        this.genericPoolParticipantRepository = genericPoolParticipantRepository;
        this.userRepository = userRepository;
        this.genericPoolRepository = genericPoolRepository;
        this.genericOptionRepository = genericOptionRepository;
        this.genericParticipantMapper = genericParticipantMapper;
    }

    @Transactional
    public GenericParticipantResponseDTO joinGenericPool(
            UUID genericPoolId,
            GenericParticipantRequestDTO dto
    ){

        //check genericPool
        GenericPool genericPool = genericPoolRepository.findById(genericPoolId)
                .orElseThrow(()-> new EntityNotFoundException(ErrorMessages.POOL_NOT_FOUND));

        //check user
        User user = userRepository.findById(dto.userId())
                .orElseThrow(()-> new EntityNotFoundException(ErrorMessages.USER_NOT_FOUND));

        //check option
        GenericOption option = genericOptionRepository.findById(dto.chosenOptionId())
                .orElseThrow(()-> new EntityNotFoundException("Option not found."));

        //create participation
        GenericPoolParticipant participant = genericParticipantMapper.toEntity(
                dto, user, genericPool, option
        );
        genericPoolParticipantRepository.save(participant);

        return genericParticipantMapper.toResponse(participant);
    }

    public List<GenericParticipantResponseDTO> listParticipantsByGenericPoolId(UUID genericPoolId){
        return genericPoolParticipantRepository.findAllByGenericPool_Id(genericPoolId)
                .stream()
                .map(genericParticipantMapper::toResponse)
                .toList();
    }
}
