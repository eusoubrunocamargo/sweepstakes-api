package com.brunothecoder.sweepstakes.api.dto.pool.closing_pool;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ParticipantsSummaryDTO(
        @NotBlank String nickname,
        @Positive BigDecimal committedAmount,
        @Positive Double percentOfPrize
        ) {
}
