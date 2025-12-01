package com.brunothecoder.sweepstakes.api.dto.pool_generic.closing_generic_pool;

import java.math.BigDecimal;

public record ConfirmedParticipantResultDTO(
        String nickname,
        String optionLabel,
        BigDecimal prizeOnWin
) {
}
