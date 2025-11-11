package com.brunothecoder.sweepstakes.api.dto.pool_participant;

import java.math.BigDecimal;
import java.util.UUID;

public record PoolParticipantRequestDTO(
        String nickname,
        BigDecimal maxValueToBet,
        UUID organizerId,
        UUID playerId,
        String keyword
) {
}
