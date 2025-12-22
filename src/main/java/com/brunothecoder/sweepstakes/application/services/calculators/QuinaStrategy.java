package com.brunothecoder.sweepstakes.application.services.calculators;

import com.brunothecoder.sweepstakes.domain.entities.LotteryType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class QuinaStrategy implements GameDistributionStrategy {

    private static final Map<Integer, BigDecimal> PRICES = new LinkedHashMap<>();

    static {
        PRICES.put(15, new BigDecimal("9009.00"));
        PRICES.put(14, new BigDecimal("6006.00"));
        PRICES.put(13, new BigDecimal("3861.00"));
        PRICES.put(12, new BigDecimal("2376.00"));
        PRICES.put(11, new BigDecimal("1386.00"));
        PRICES.put(10, new BigDecimal("756.00"));
        PRICES.put(9, new BigDecimal("378.00"));
        PRICES.put(8, new BigDecimal("168.00"));
        PRICES.put(7, new BigDecimal("63.00"));
        PRICES.put(6, new BigDecimal("18.00"));
        PRICES.put(5, new BigDecimal("3.00"));
    }

    @Override
    public LotteryType getLotteryType(){
        return LotteryType.QUINA;
    }

    @Override
    public GameDistributionResult calculate (BigDecimal totalAmount){
        BigDecimal remaining = totalAmount;
        Map<Integer,Integer> distribution = new LinkedHashMap<>();

        for(int numbers = 15; numbers >=5; numbers--){
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
