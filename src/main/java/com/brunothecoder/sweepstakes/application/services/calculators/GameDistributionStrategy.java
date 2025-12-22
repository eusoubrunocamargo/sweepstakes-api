package com.brunothecoder.sweepstakes.application.services.calculators;

import com.brunothecoder.sweepstakes.domain.entities.LotteryType;

import java.math.BigDecimal;

public interface GameDistributionStrategy {
    LotteryType getLotteryType();
    GameDistributionResult calculate(BigDecimal totalAmount);
}
