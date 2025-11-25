package com.brunothecoder.sweepstakes.application.services.calculators;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ParticipantShareCalculator {

    public double calculatePercent (BigDecimal committedAmount, BigDecimal grossAmountCollected) {

        if(grossAmountCollected == null || grossAmountCollected.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }

        return committedAmount
                .divide(grossAmountCollected, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

    }
}
