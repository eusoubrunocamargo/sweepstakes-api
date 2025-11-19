package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.megasena.GameDistributionResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import com.brunothecoder.sweepstakes.api.mappers.GameDistributionMapper;
import com.brunothecoder.sweepstakes.api.mappers.PoolMapper;
import com.brunothecoder.sweepstakes.api.mappers.PoolParticipantMapper;
import com.brunothecoder.sweepstakes.application.services.cache.PoolCacheService;
import com.brunothecoder.sweepstakes.application.services.calculators.GameDistributionResult;
import com.brunothecoder.sweepstakes.application.services.calculators.MegaSenaCalculator;
import com.brunothecoder.sweepstakes.domain.entities.*;
import com.brunothecoder.sweepstakes.domain.repositories.PoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolRepository;
import com.brunothecoder.sweepstakes.domain.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PoolService {

//    @Autowired
//    private MegaSenaCalculator megaSenaCalculator;

    private final PoolRepository poolRepository;
    private final PoolMapper poolMapper;
    private final UserRepository userRepository;
    private final PoolParticipantRepository poolParticipantRepository;
//    private final PoolCacheService poolCacheService;
    private final MegaSenaCalculator megaSenaCalculator;
    private final PoolParticipantMapper poolParticipantMapper;
    private final FinancialService financialService;
    private final BigDecimal PLATFORM_FEE = BigDecimal.valueOf(0.05);

    public PoolService(
            PoolRepository poolRepository,
            PoolMapper poolMapper,
            UserRepository userRepository,
            PoolParticipantRepository poolParticipantRepository,
            PoolParticipantMapper poolParticipantMapper,
            PoolCacheService poolCacheService,
            MegaSenaCalculator megaSenaCalculator,
            FinancialService financialService
    ){
        this.poolRepository = poolRepository;
        this.poolMapper = poolMapper;
        this.userRepository = userRepository;
        this.poolParticipantRepository = poolParticipantRepository;
//        this.poolCacheService = poolCacheService;
        this.poolParticipantMapper = poolParticipantMapper;
        this.megaSenaCalculator = megaSenaCalculator;
        this.financialService = financialService;
    }

    @Transactional
    public PoolResponseDTO createPool(PoolRequestDTO dto){
        //check if user exists
        User user = userRepository.findById(dto.userId())
                .orElseThrow(()-> new EntityNotFoundException(ErrorMessages.USER_NOT_FOUND));
        //give organizer role
        if(!user.getRoles().contains(Role.ORGANIZER)){
            user.getRoles().add(Role.ORGANIZER);
            userRepository.save(user);
        }
        Pool pool = poolMapper.toEntity(dto, user);
        poolRepository.save(pool);

        //optional participation of pools creator
        boolean includeCreator = Boolean.TRUE.equals(dto.includeCreatorAsParticipant());
        if(includeCreator){
            String nickname = dto.creatorParticipation().nickname();
            BigDecimal maxValue = dto.creatorParticipation().maxValueToBet();

            if(!poolParticipantRepository.existsByPoolIdAndPlayerId(pool.getId(), user.getId())){
                PoolParticipant participant =
                        poolParticipantMapper.toEntity(
                            new PoolParticipantRequestDTO(
                                    nickname,
                                    maxValue,
                                    user.getId(),
                                    dto.keyword()),
                                    user,
                                    pool
                        );
                participant.setJoinedAt(LocalDateTime.now());
                poolParticipantRepository.save(participant);
            }
        }

        return poolMapper.toResponse(pool);
    }

    public List<PoolResponseDTO> listAllPools(){
        return poolRepository.findAll().stream().map(poolMapper::toResponse).toList();
    }

    public BigDecimal calculateTotalAmount(UUID poolId){
        //calc total amount

        //cache totalAmount
        //poolCacheService.cachePoolStats(poolId, totalAmount);

//        return poolParticipantRepository.findAllByPoolId(poolId)
//                .stream()
//                .map(PoolParticipant::getMaxValueToBet)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = poolParticipantRepository.getConfirmedTotalAmount(poolId);
        return Objects.requireNonNullElse(total, BigDecimal.ZERO);
    }

    public BigDecimal getCachedTotalAmount(UUID poolId){
        return calculateTotalAmount(poolId);
    }

    public GameDistributionResponseDTO calculateGameDistribution(UUID poolId){

        Pool pool = poolRepository.findById(poolId).orElseThrow(()-> new EntityNotFoundException("Pool not found!"));

        //Gross amount from confirmed participants
        BigDecimal confirmedGrossAmount = poolParticipantRepository.getConfirmedTotalAmount(poolId);
        if(confirmedGrossAmount == null) confirmedGrossAmount = BigDecimal.ZERO;

        //Apply platform tax
        BigDecimal netAmountForBetting = financialService.calculateNetAmountForBetting
                (confirmedGrossAmount, pool.getAdminFeePercentage());

//        GameDistributionResult result = megaSenaCalculator.calculate(calculateTotalAmount(poolId));
        GameDistributionResult result = megaSenaCalculator.calculate(netAmountForBetting);
        return GameDistributionMapper.toResponse(
                pool,
                result,
                confirmedGrossAmount,
                netAmountForBetting);
    }

}
