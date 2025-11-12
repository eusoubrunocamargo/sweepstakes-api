package com.brunothecoder.sweepstakes.application.services.calculators;

import java.math.BigDecimal;
import java.util.Map;

public record GameDistributionResult(
        BigDecimal totalAmount,
        BigDecimal totalSpent,
        BigDecimal remaining,
        Map<Integer, Integer> distribution) {
}
