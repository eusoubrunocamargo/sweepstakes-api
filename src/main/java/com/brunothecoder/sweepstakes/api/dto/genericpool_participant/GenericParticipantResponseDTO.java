package com.brunothecoder.sweepstakes.api.dto.genericpool_participant;

import java.time.LocalDateTime;
import java.util.UUID;

public record GenericParticipantResponseDTO(
        UUID id,
        String nickname,
        UUID userId,
        UUID genericPoolId,
        UUID chosenOptionId,
        LocalDateTime joinedAt
) {
}
