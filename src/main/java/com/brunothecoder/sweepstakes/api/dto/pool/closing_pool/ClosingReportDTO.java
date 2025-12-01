package com.brunothecoder.sweepstakes.api.dto.pool.closing_pool;

import com.brunothecoder.sweepstakes.api.dto.megasena.GameDistributionResponseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record ClosingReportDTO(
        @NotNull String poolName,
        @Positive BigDecimal grossAmountCollected,
        @Positive BigDecimal platformFee,
        @Positive BigDecimal netAmountForBetting,
        @Positive BigDecimal amountSpentOnGames,
        @Positive BigDecimal netBalanceRemaining,
        @NotNull List<GameDistributionResponseDTO> distribution,
        @NotNull ParticipantsDTO participants
        ) {
}
