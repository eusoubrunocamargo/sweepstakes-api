package com.brunothecoder.sweepstakes.api.dto.pool_participant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PoolParticipantResponseDTO(
        UUID id,
        UUID playerId,
        UUID poolId,
        String nickname,
        BigDecimal maxValueToBet,
        LocalDateTime joinedAt
) {
}
