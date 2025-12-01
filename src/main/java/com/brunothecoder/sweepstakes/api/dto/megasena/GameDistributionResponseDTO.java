package com.brunothecoder.sweepstakes.api.dto.megasena;

import java.math.BigDecimal;
import java.util.List;

public record GameDistributionResponseDTO(
        String poolName,
//        BigDecimal totalAmount,
//        BigDecimal totalSpent,
//        BigDecimal remaining,
        BigDecimal grossAmountCollected,
        BigDecimal platformFee,
        BigDecimal netAmountForBetting,
        BigDecimal amountSpentOnGames,
        BigDecimal netBalanceRemaining,

        List<GameDetailDTO> distribution
) {
}
