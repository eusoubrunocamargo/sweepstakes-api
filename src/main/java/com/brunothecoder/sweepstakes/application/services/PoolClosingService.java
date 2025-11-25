package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.megasena.GameDistributionResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.closing_pool.ClosingReportDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.closing_pool.ParticipantsDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.closing_pool.ParticipantsSummaryDTO;
import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import com.brunothecoder.sweepstakes.application.services.calculators.ParticipantShareCalculator;
import com.brunothecoder.sweepstakes.domain.entities.*;
import com.brunothecoder.sweepstakes.domain.repositories.PoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PoolClosingService {

    private final PoolRepository poolRepository;
    private final PoolParticipantRepository poolParticipantRepository;
    private final FinancialService financialService;
    private final PoolService poolService;
    private final ParticipantShareCalculator participantShareCalculator;

    public PoolClosingService(
            PoolRepository poolRepository,
            PoolParticipantRepository poolParticipantRepository,
            FinancialService financialService,
            PoolService poolService,
            ParticipantShareCalculator participantShareCalculator
    ) {
        this.poolRepository = poolRepository;
        this.poolParticipantRepository = poolParticipantRepository;
        this.financialService = financialService;
        this.poolService = poolService;
        this.participantShareCalculator = participantShareCalculator;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void closeExpiredPools() {
        List<Pool> expiredPools = poolRepository.findAllExpiredPools(LocalDateTime.now());
        for (Pool pool : expiredPools) {
            processPoolClosure(pool);
        }
    }

    public void processPoolClosure(Pool pool) {

        pool.setStatus(PoolStatus.FINALIZED);
        poolRepository.save(pool);
        generateClosingReport(pool.getId());

    }

    public ClosingReportDTO generateClosingReport(UUID poolId) {

        //search pool
        Pool pool = poolRepository.findById(poolId)
                .orElseThrow(()-> new EntityNotFoundException(ErrorMessages.POOL_NOT_FOUND));

        //calc financial values
        BigDecimal grossAmountCollected = poolParticipantRepository.getConfirmedTotalAmount(poolId);
        if(grossAmountCollected == null || grossAmountCollected.compareTo(BigDecimal.ZERO) == 0) {
            grossAmountCollected = BigDecimal.ZERO;
        }
        final BigDecimal totalAmount = grossAmountCollected;

        BigDecimal netAmountForBetting = financialService.calculateNetAmountForBetting(totalAmount, pool.getAdminFeePercentage());
        BigDecimal platformFee = totalAmount.multiply(BigDecimal.valueOf(0.05));

        //calc distribution from pool service
        GameDistributionResponseDTO distributionDTO = poolService.calculateGameDistribution(poolId);
        List<GameDistributionResponseDTO> distributionList = List.of(distributionDTO);

        //search and separate confirmed/canceled participants
        List<PoolParticipant> participants = poolParticipantRepository.findAllByPoolId(poolId);

        List<ParticipantsSummaryDTO> confirmed = participants.stream()
                .filter(p -> p.getStatus() == ParticipantStatus.CONFIRMED)
                .map(p -> new ParticipantsSummaryDTO(
                        p.getNickname(),
                        p.getMaxValueToBet(),
                        participantShareCalculator.calculatePercent(p.getMaxValueToBet(), totalAmount)
                ))
                .toList();


        List<ParticipantsSummaryDTO> canceled = participants.stream()
                .filter(p -> p.getStatus() == ParticipantStatus.EXPIRED)
                .map(p -> new ParticipantsSummaryDTO(p.getNickname(), p.getMaxValueToBet(), 0.0))
                .toList();

        ParticipantsDTO participantsDTO = new ParticipantsDTO(confirmed,canceled);

        return new ClosingReportDTO(
                pool.getName(),
                totalAmount,
                platformFee,
                netAmountForBetting,
                distributionDTO.amountSpentOnGames(),
                distributionDTO.netBalanceRemaining(),
                distributionList,
                participantsDTO
        );
    }

}
