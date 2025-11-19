package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.megasena.GameDetailDTO;
import com.brunothecoder.sweepstakes.api.dto.megasena.GameDistributionResponseDTO;
import com.brunothecoder.sweepstakes.application.services.calculators.GameDistributionResult;
import com.brunothecoder.sweepstakes.application.services.calculators.MegaSenaCalculator;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class GameDistributionMapper {

    public static GameDistributionResponseDTO toResponse(
            Pool pool,
            GameDistributionResult result,
            BigDecimal grossAmountCollected,
            BigDecimal netAmountForBetting
    ){

        //calc final value
        BigDecimal platformFee = grossAmountCollected.subtract(netAmountForBetting)
                .setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal amountSpentOnGames = result.totalSpent();

        BigDecimal netBalanceRemaining = netAmountForBetting.subtract(amountSpentOnGames)
                .setScale(2, RoundingMode.HALF_EVEN);

        //mapping
        List<GameDetailDTO> distribution = result
                .distribution()
                .entrySet()
                .stream()
                .map(entry -> {
                    int numbers = entry.getKey();
                    int quantity = entry.getValue();
                    BigDecimal cost = MegaSenaCalculator
                            .getPrice(numbers)
                            .multiply(BigDecimal.valueOf(quantity));
                    return new GameDetailDTO(numbers, quantity, cost);
                }
        ).toList();

        return new GameDistributionResponseDTO(
                pool.getName(),
                grossAmountCollected,
                platformFee,
                netAmountForBetting,
                amountSpentOnGames,
                netBalanceRemaining,
                distribution
        );
    }
}
