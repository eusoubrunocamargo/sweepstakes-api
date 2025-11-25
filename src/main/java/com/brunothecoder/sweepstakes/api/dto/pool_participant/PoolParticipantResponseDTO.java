package com.brunothecoder.sweepstakes.api.dto.pool_participant;

import com.brunothecoder.sweepstakes.domain.entities.ParticipantStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PoolParticipantResponseDTO(
        UUID id,
        UUID playerId,
        UUID poolId,
        String nickname,
        BigDecimal maxValueToBet,
        ParticipantStatus status,
        LocalDateTime joinedAt
) {
}
