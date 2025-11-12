package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.megasena.GameDistributionResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
import com.brunothecoder.sweepstakes.api.mappers.GameDistributionMapper;
import com.brunothecoder.sweepstakes.api.mappers.PoolMapper;
import com.brunothecoder.sweepstakes.application.services.cache.PoolCacheService;
import com.brunothecoder.sweepstakes.application.services.calculators.GameDistributionResult;
import com.brunothecoder.sweepstakes.application.services.calculators.MegaSenaCalculator;
import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import com.brunothecoder.sweepstakes.domain.repositories.OrganizerRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PoolService {

//    @Autowired
//    private MegaSenaCalculator megaSenaCalculator;

    private final PoolRepository poolRepository;
    private final PoolMapper poolMapper;
    private final OrganizerRepository organizerRepository;
    private final PoolParticipantRepository poolParticipantRepository;
    private final PoolCacheService poolCacheService;
    private final MegaSenaCalculator megaSenaCalculator;

    public PoolService(
            PoolRepository poolRepository,
            PoolMapper poolMapper,
            OrganizerRepository organizerRepository,
            PoolParticipantRepository poolParticipantRepository,
            PoolCacheService poolCacheService,
            MegaSenaCalculator megaSenaCalculator
    ){
        this.poolRepository = poolRepository;
        this.poolMapper = poolMapper;
        this.organizerRepository = organizerRepository;
        this.poolParticipantRepository = poolParticipantRepository;
        this.poolCacheService = poolCacheService;
        this.megaSenaCalculator = megaSenaCalculator;
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

    public BigDecimal calculateTotalAmount(UUID poolId){
        //calc total amount
        BigDecimal totalAmount = poolParticipantRepository.findAllByPoolId(poolId)
                .stream()
                .map(PoolParticipant::getMaxValueToBet)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //cache totalAmount
        poolCacheService.cachePoolStats(poolId, totalAmount);

        return totalAmount;
    }

    public BigDecimal getCachedTotalAmount(UUID poolId){
        return calculateTotalAmount(poolId);
    }

    public GameDistributionResponseDTO calculateGameDistribution(UUID poolId){
        Pool pool = poolRepository.findById(poolId).orElseThrow(()-> new EntityNotFoundException("Pool not found!"));
        GameDistributionResult result = megaSenaCalculator.calculate(calculateTotalAmount(poolId));
        return GameDistributionMapper.toResponse(pool, result);
    }

}
