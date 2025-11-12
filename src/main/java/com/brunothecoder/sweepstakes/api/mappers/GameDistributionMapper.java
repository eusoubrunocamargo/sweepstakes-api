package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.megasena.GameDetailDTO;
import com.brunothecoder.sweepstakes.api.dto.megasena.GameDistributionResponseDTO;
import com.brunothecoder.sweepstakes.application.services.calculators.GameDistributionResult;
import com.brunothecoder.sweepstakes.application.services.calculators.MegaSenaCalculator;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class GameDistributionMapper {

    public static GameDistributionResponseDTO toResponse(
            Pool pool,
            GameDistributionResult result
    ){
        List<GameDetailDTO> details = result
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
                result.totalAmount(),
                result.totalSpent(),
                result.remaining(),
                details
        );
    }
}
