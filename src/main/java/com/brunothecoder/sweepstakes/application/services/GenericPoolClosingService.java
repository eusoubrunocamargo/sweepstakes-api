package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.pool_generic.closing_generic_pool.ConfirmedParticipantResultDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.closing_generic_pool.ExpiredParticipantDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.closing_generic_pool.GenericClosingReportDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.closing_generic_pool.OptionDTO;
import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import com.brunothecoder.sweepstakes.domain.entities.GenericPool;
import com.brunothecoder.sweepstakes.domain.entities.GenericPoolParticipant;
import com.brunothecoder.sweepstakes.domain.entities.ParticipantStatus;
import com.brunothecoder.sweepstakes.domain.repositories.GenericPoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.GenericPoolRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GenericPoolClosingService {

    private final GenericPoolRepository genericPoolRepository;
    private final GenericPoolParticipantRepository genericPoolParticipantRepository;
    private final FinancialService financialService;

    public GenericPoolClosingService(
            GenericPoolRepository genericPoolRepository,
            GenericPoolParticipantRepository genericPoolParticipantRepository,
            FinancialService financialService
    ){
        this.genericPoolRepository = genericPoolRepository;
        this.genericPoolParticipantRepository = genericPoolParticipantRepository;
        this.financialService = financialService;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void closeExpiredGenericPools() {
        List<GenericPool> expiredGenericPools =
                genericPoolRepository.findAllExpiredGenericPools(LocalDateTime.now());
        expiredGenericPools.forEach(this::processGenericPoolClosure);
    }

    @Transactional
    private void processGenericPoolClosure(GenericPool genericPool) {

        //finalize generic pools
        genericPool.setFinalized(true);
        genericPoolRepository.save(genericPool);

        //update participants: pending -> expired
        genericPoolParticipantRepository.expirePendingParticipants(
                genericPool.getId()
        );

        //generate report
        generateClosingReport(genericPool.getId());
    }

    @Transactional
    public GenericClosingReportDTO generateClosingReport(UUID poolId) {

        //check if generic pools exists
        GenericPool genericPool = genericPoolRepository.findById(poolId)
                .orElseThrow(()-> new EntityNotFoundException(ErrorMessages.POOL_NOT_FOUND));

        //get confirmed total amount
        BigDecimal grossAmount = genericPoolParticipantRepository.getConfirmedTotalAmount(poolId);
        if(grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) == 0){
            grossAmount = BigDecimal.ZERO;
        }
        final BigDecimal totalAmount = grossAmount;

        //get net amount and platform fee
        BigDecimal netAmount = financialService.calculateNetAmountForBetting(totalAmount, genericPool.getAdminFeePercentage());
        BigDecimal platformFee = totalAmount.multiply(BigDecimal.valueOf(0.05));

        //get participants
        List<GenericPoolParticipant> participants = genericPoolParticipantRepository.findAllByGenericPool_Id(poolId);

        //get prize by scenarios
        Map<String, BigDecimal> prizeMap = financialService.getPrizeOnWin(poolId);

        //create options list
        List<OptionDTO> options = genericPool.getOptions()
                .stream()
                .map(opt -> new OptionDTO(opt.getLabel()))
                .toList();

        //List of confirmed with prize
        List<ConfirmedParticipantResultDTO> confirmedParticipants = participants
                .stream()
                .filter(p -> p.getStatus() == ParticipantStatus.CONFIRMED)
                .map(p -> new ConfirmedParticipantResultDTO(
                        p.getNickname(),
                        p.getChosenOption().getLabel(),
                        prizeMap.getOrDefault(p.getChosenOption().getLabel(), BigDecimal.ZERO)
                ))
                .toList();

        //list of expired/canceled
        List<ExpiredParticipantDTO> expiredParticipants = participants
                .stream()
                .filter(p ->
                        p.getStatus() == ParticipantStatus.EXPIRED ||
                                p.getStatus() == ParticipantStatus.CANCELED)
                .map(p -> new ExpiredParticipantDTO(p.getNickname()))
                .toList();

        return new GenericClosingReportDTO(
                genericPool.getName(),
                genericPool.getDescription(),
                genericPool.getOrganizer().getName(),
                genericPool.getPoolValue(),
                totalAmount,
                platformFee,
                netAmount,
                genericPool.getEndDate(),
                genericPool.getDrawDate(),
                genericPool.getCreatedAt(),
                options,
                confirmedParticipants,
                expiredParticipants
        );
    }



}
