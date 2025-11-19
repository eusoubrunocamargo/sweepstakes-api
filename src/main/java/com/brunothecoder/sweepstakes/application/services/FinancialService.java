package com.brunothecoder.sweepstakes.application.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FinancialService {

    public BigDecimal calculateNetAmountForBetting(BigDecimal grossAmount, BigDecimal feePercentage) {
        if(grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0){
            return BigDecimal.ZERO;
        }

        //define multiplier
        BigDecimal netMultiplier = BigDecimal.ONE.subtract(feePercentage);

        //net amount
        return grossAmount.multiply(netMultiplier).setScale(2, RoundingMode.HALF_EVEN);

    }
}
