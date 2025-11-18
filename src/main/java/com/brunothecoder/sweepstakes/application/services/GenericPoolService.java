package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericOptionRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolResponseDTO;
import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import com.brunothecoder.sweepstakes.api.mappers.GenericOptionMapper;
import com.brunothecoder.sweepstakes.api.mappers.GenericPoolMapper;
import com.brunothecoder.sweepstakes.domain.entities.GenericOption;
import com.brunothecoder.sweepstakes.domain.entities.GenericPool;
import com.brunothecoder.sweepstakes.domain.entities.User;
import com.brunothecoder.sweepstakes.domain.repositories.GenericOptionRepository;
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

import static java.util.stream.Collectors.toList;

@Service
public class GenericPoolService {

    private final GenericOptionRepository genericOptionRepository;
    private final GenericPoolRepository genericPoolRepository;
    private final GenericPoolMapper genericPoolMapper;
    private final GenericOptionMapper genericOptionMapper;
    private final UserRepository userRepository;

    public GenericPoolService(
            GenericPoolRepository genericPoolRepository,
            GenericOptionRepository genericOptionRepository,
            GenericPoolMapper genericPoolMapper,
            GenericOptionMapper genericOptionMapper,
            UserRepository userRepository
    ){
        this.genericOptionMapper = genericOptionMapper;
        this.genericPoolMapper = genericPoolMapper;
        this.genericPoolRepository = genericPoolRepository;
        this.genericOptionRepository = genericOptionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public GenericPoolResponseDTO create(GenericPoolRequestDTO dto){

        //check user
        User user = userRepository.findById(dto.userId())
                .orElseThrow(()-> new EntityNotFoundException(ErrorMessages.USER_NOT_FOUND));

        //check options
        validateOptions(dto.options());

        //create generic pool
        GenericPool genericPool = genericPoolMapper.toEntity(dto, user);
        genericPoolRepository.save(genericPool);

        //create options
        List<GenericOption> options = dto.options()
                .stream()
                .map(opt -> genericOptionMapper.toEntity(opt, genericPool))
                .toList();
        genericOptionRepository.saveAll(options);


        return genericPoolMapper.toResponse(genericPool, options);
    }

    @Transactional
    public List<GenericPoolResponseDTO> listAllGenericPools(){
        return genericPoolRepository.findAll()
                .stream()
                .map(genericPool -> {
                    List<GenericOption> options =
                            genericOptionRepository.findAllByPoolGenericId(genericPool.getId());
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
}
