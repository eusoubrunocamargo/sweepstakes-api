package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.pool.PoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
import com.brunothecoder.sweepstakes.api.mappers.PoolMapper;
import com.brunothecoder.sweepstakes.application.services.cache.PoolCacheService;
import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import com.brunothecoder.sweepstakes.domain.repositories.OrganizerRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class PoolService {

    private final PoolRepository poolRepository;
    private final PoolMapper poolMapper;
    private final OrganizerRepository organizerRepository;
    private final PoolParticipantRepository poolParticipantRepository;
    private final PoolCacheService poolCacheService;

    public PoolService(
            PoolRepository poolRepository,
            PoolMapper poolMapper,
            OrganizerRepository organizerRepository,
            PoolParticipantRepository poolParticipantRepository,
            PoolCacheService poolCacheService
    ){
        this.poolRepository = poolRepository;
        this.poolMapper = poolMapper;
        this.organizerRepository = organizerRepository;
        this.poolParticipantRepository = poolParticipantRepository;
        this.poolCacheService = poolCacheService;
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
//        Map<Object, Object> stats = poolCacheService.getPoolStats(poolId);
//        if(stats.containsKey("totalAmount")){
//            return new BigDecimal(stats.get("totalAmount").toString());
//        }
//        throw new IllegalStateException("No cached totalAmount for pool " + poolId);
    }
}
