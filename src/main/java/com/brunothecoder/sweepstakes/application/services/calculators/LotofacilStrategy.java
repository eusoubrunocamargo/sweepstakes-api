package com.brunothecoder.sweepstakes.application.services.calculators;

import com.brunothecoder.sweepstakes.domain.entities.LotteryType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LotofacilStrategy implements GameDistributionStrategy {

        private static final Map<Integer, BigDecimal> PRICES = new LinkedHashMap<>();

        static {
            PRICES.put(20, new BigDecimal("54264.00"));
            PRICES.put(19, new BigDecimal("13566.00"));
            PRICES.put(18, new BigDecimal("2856.00"));
            PRICES.put(17, new BigDecimal("476.00"));
            PRICES.put(16, new BigDecimal("56.00"));
            PRICES.put(15, new BigDecimal("3.50"));
        }

        @Override
        public LotteryType getLotteryType(){
            return LotteryType.LOTOFACIL;
        }

        @Override
        public GameDistributionResult calculate (BigDecimal totalAmount){
            BigDecimal remaining = totalAmount;
            Map<Integer,Integer> distribution = new LinkedHashMap<>();

            for(int numbers = 20; numbers >=15; numbers--){
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


