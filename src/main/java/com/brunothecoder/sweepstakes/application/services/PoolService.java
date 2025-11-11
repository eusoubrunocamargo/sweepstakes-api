package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.pool.PoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
import com.brunothecoder.sweepstakes.api.mappers.PoolMapper;
import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.repositories.OrganizerRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoolService {

    private final PoolRepository poolRepository;
    private final PoolMapper poolMapper;
    private final OrganizerRepository organizerRepository;

    public PoolService(
            PoolRepository poolRepository,
            PoolMapper poolMapper,
            OrganizerRepository organizerRepository
    ){
        this.poolRepository = poolRepository;
        this.poolMapper = poolMapper;
        this.organizerRepository = organizerRepository;
    }

    public PoolResponseDTO createPool(PoolRequestDTO dto){
        Organizer organizer = organizerRepository.findById(dto.organizerId())
                .orElseThrow(()-> new EntityNotFoundException("Organizer not found"));

        Pool pool = poolMapper.toEntity(dto, organizer);
        poolRepository.save(pool);

        return poolMapper.toResponse(pool);
    }

    public List<PoolResponseDTO> listAllPools(){
        return poolRepository.findAll().stream().map(poolMapper::toResponse).toList();
    }
}
