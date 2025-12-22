package com.brunothecoder.sweepstakes.application.services.calculators;

import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import com.brunothecoder.sweepstakes.domain.entities.LotteryType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GameDistributionStrategyFactory {

    private final Map<LotteryType,GameDistributionStrategy> strategies;

    public GameDistributionStrategyFactory (List<GameDistributionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(GameDistributionStrategy::getLotteryType, Function.identity()));
    }

    public GameDistributionStrategy getStrategy(LotteryType type) {
        GameDistributionStrategy strategy = strategies.get(type);
        if(strategy == null) {
            throw new IllegalArgumentException(ErrorMessages.NO_CALCULATOR_FOR_THIS_TYPE);
        }
        return strategy;
    }
}
