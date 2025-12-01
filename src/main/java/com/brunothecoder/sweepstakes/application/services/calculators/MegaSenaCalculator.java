package com.brunothecoder.sweepstakes.application.services.calculators;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class MegaSenaCalculator {

    private static final Map<Integer, BigDecimal> PRICES = Map.ofEntries(
            Map.entry(6,BigDecimal.valueOf(6.00)),
            Map.entry(7,BigDecimal.valueOf(42.00)),
            Map.entry(8,BigDecimal.valueOf(168.00)),
            Map.entry(9,BigDecimal.valueOf(504.00)),
            Map.entry(10,BigDecimal.valueOf(1260.00)),
            Map.entry(11,BigDecimal.valueOf(2772.00)),
            Map.entry(12,BigDecimal.valueOf(5544.00)),
            Map.entry(13,BigDecimal.valueOf(10296.00)),
            Map.entry(14,BigDecimal.valueOf(18018.00)),
            Map.entry(15,BigDecimal.valueOf(30030.00)),
            Map.entry(16,BigDecimal.valueOf(48048.00)),
            Map.entry(17, BigDecimal.valueOf(74256.00)),
            Map.entry(18, BigDecimal.valueOf(111384.00)),
            Map.entry(19, BigDecimal.valueOf(162792.00)),
            Map.entry(20, BigDecimal.valueOf(232560.00))
    );

    public GameDistributionResult calculate (BigDecimal totalAmount){
        BigDecimal remaining = totalAmount;
        Map<Integer,Integer> distribution = new LinkedHashMap<>();

        for(int numbers = 20; numbers >=6; numbers--){
            BigDecimal price = PRICES.get(numbers);
            int count = remaining.divide(price, RoundingMode.DOWN).intValue();
            if(count > 0){
                distribution.put(numbers, count);
                remaining = remaining.subtract(price.multiply(BigDecimal.valueOf(count)));
            }
        }

        BigDecimal spent = totalAmount.subtract(remaining);
        return new GameDistributionResult(totalAmount, spent, remaining, distribution);
    }

    public static BigDecimal getPrice(int numbers){
        return PRICES.get(numbers);
    }
}
