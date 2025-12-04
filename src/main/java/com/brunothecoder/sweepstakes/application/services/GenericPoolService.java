package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.genericpool_participant.GenericParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericOptionRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolResponseDTO;
import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import com.brunothecoder.sweepstakes.api.mappers.GenericOptionMapper;
import com.brunothecoder.sweepstakes.api.mappers.GenericPoolMapper;
import com.brunothecoder.sweepstakes.domain.entities.*;
import com.brunothecoder.sweepstakes.domain.repositories.GenericOptionRepository;
import com.brunothecoder.sweepstakes.domain.repositories.GenericPoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.GenericPoolRepository;
import com.brunothecoder.sweepstakes.domain.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GenericPoolService {

    private final GenericOptionRepository genericOptionRepository;
    private final GenericPoolRepository genericPoolRepository;
    private final GenericPoolMapper genericPoolMapper;
    private final GenericOptionMapper genericOptionMapper;
    private final UserRepository userRepository;
    private final GenericPoolParticipantRepository genericPoolParticipantRepository;
    private final GenericPoolParticipantService genericPoolParticipantService;
    private static final int MAX_ACTIVE_POOLS = 5;

    public GenericPoolService(
            GenericPoolRepository genericPoolRepository,
            GenericPoolParticipantService genericPoolParticipantService,
            GenericOptionRepository genericOptionRepository,
            GenericPoolMapper genericPoolMapper,
            GenericOptionMapper genericOptionMapper,
            UserRepository userRepository,
            GenericPoolParticipantRepository genericPoolParticipantRepository
    ){
        this.genericOptionMapper = genericOptionMapper;
        this.genericPoolParticipantRepository = genericPoolParticipantRepository;
        this.genericPoolMapper = genericPoolMapper;
        this.genericPoolRepository = genericPoolRepository;
        this.genericOptionRepository = genericOptionRepository;
        this.userRepository = userRepository;
        this.genericPoolParticipantService = genericPoolParticipantService;
    }

    @Transactional
    public GenericPoolResponseDTO create(GenericPoolRequestDTO dto){

        // 1. check and validate user
        User user = userRepository.findById(dto.userId())
                .orElseThrow(()-> new EntityNotFoundException(ErrorMessages.USER_NOT_FOUND));

        // 2. check and validate options
        validateOptions(dto.options());

        // 3. check if already exists pool with same name and same user
        if(genericPoolRepository.existsByNameAndStatusAndOrganizer_Id(
                dto.name(),
                PoolStatus.OPEN,
                user.getId())){
            throw new IllegalArgumentException(ErrorMessages.POOL_ALREADY_EXISTS);
        }

        // 4. check if max 5 active pools is reached
        long activePools = genericPoolRepository.countByOrganizer_IdAndStatus(user.getId(),PoolStatus.OPEN);
        if(activePools >= MAX_ACTIVE_POOLS) {
            throw new IllegalArgumentException(ErrorMessages.MAX_ACTIVE_POOLS);
        }

        // 5. promote to organizer
        if(user.promoteToOrganizer()) {
            userRepository.save(user);
        }

        // 6. create generic pool (validations with @PrePersist)
        GenericPool genericPool = genericPoolMapper.toEntity(dto, user);
        genericPool.setStatus(PoolStatus.OPEN);
        genericPoolRepository.save(genericPool);

        // 7. create and save options
        List<GenericOption> options = dto.options()
                .stream()
                .map(opt -> genericOptionMapper.toEntity(opt, genericPool))
                .toList();
        genericOptionRepository.saveAll(options);

        // 8. Add creator as participant (if true)
        addCreatorAsParticipantIfChosen(genericPool, user, options);

        // 9. return answer
        return genericPoolMapper.toResponse(genericPool, options);

    }

    @Transactional
    public List<GenericPoolResponseDTO> listAllGenericPools(){
        return genericPoolRepository.findAll()
                .stream()
                .map(genericPool -> {
                    List<GenericOption> options =
                            genericOptionRepository.findByGenericPool_Id(genericPool.getId());
                    return genericPoolMapper.toResponse(genericPool, options);
                })
                .toList();


    }

    private void validateOptions(@Size(min = 2, message = "Pool must have at least 2 options.") @Valid List<GenericOptionRequestDTO> options) {

        if(options.size() < 2){
            throw new IllegalArgumentException("Pool must have at least 2 options.");
        };
        Set<String> labels = new HashSet<>();
        boolean creatorChoiceFound = false;
        for (GenericOptionRequestDTO opt : options){
            String normalized = opt.label().trim().toLowerCase();
            if(!labels.add(normalized)) {
                throw new IllegalArgumentException("Duplicate option: " + opt.label());
            }
            if(Boolean.TRUE.equals(opt.isCreatorChoice())){
                if(creatorChoiceFound){
                    throw new IllegalArgumentException("Only one creator choice allowed.");
                }
                creatorChoiceFound = true;
            }
        }
    }

    private void addCreatorAsParticipantIfChosen(
            GenericPool genericPool,
            User user,
            List<GenericOption> options) {

        options.stream()
                .filter(opt-> Boolean.TRUE.equals(opt.getCreatorChoice()))
                .findFirst()
                .ifPresent(creatorOption -> {
                    GenericParticipantRequestDTO participantRequestDTO =
                            new GenericParticipantRequestDTO(
                                    user.getName(),
                                    user.getId(),
                                    creatorOption.getId()
                            );

                    genericPoolParticipantService.joinGenericPool(
                            genericPool.getId(),
                            participantRequestDTO
                    );
                });
    }
}
