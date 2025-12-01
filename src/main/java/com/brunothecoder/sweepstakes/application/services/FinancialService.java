package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.domain.repositories.GenericPoolParticipantRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FinancialService {

    private final GenericPoolParticipantRepository genericPoolParticipantRepository;

    public FinancialService(GenericPoolParticipantRepository genericPoolParticipantRepository){
        this.genericPoolParticipantRepository = genericPoolParticipantRepository;
    }
    public BigDecimal calculateNetAmountForBetting(BigDecimal grossAmount, BigDecimal feePercentage) {
        if(grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0){
            return BigDecimal.ZERO;
        }

        //define multiplier
        BigDecimal netMultiplier = BigDecimal.ONE.subtract(feePercentage);

        //net amount
        return grossAmount.multiply(netMultiplier).setScale(2, RoundingMode.HALF_EVEN);

    }

    public Map<String,BigDecimal> getPrizeOnWin(UUID genericPoolId) {

        //get total confirmed
        BigDecimal totalConfirmed = genericPoolParticipantRepository.getConfirmedTotalAmount(genericPoolId);

        //get confirmedByOption
        List<GenericPoolParticipantRepository.OptionCountProjection> projectionList =
                genericPoolParticipantRepository.countConfirmedByOption(genericPoolId);
        Map<String, Long> participantsByOption = projectionList.stream()
                .collect(Collectors.toMap(
                        GenericPoolParticipantRepository.OptionCountProjection::getOptionLabel,
                        GenericPoolParticipantRepository.OptionCountProjection::getCount)
                );

        //set prize for each participant in case of victory
        Map<String, BigDecimal> prizeMap = new HashMap<>();
        participantsByOption.forEach((optionLabel, count) -> {
            BigDecimal prize = totalConfirmed.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            prizeMap.put(optionLabel, prize);
        });

        return prizeMap;

    };
}
