package com.brunothecoder.sweepstakes.api.dto.pool_generic;

import java.util.UUID;

public record GenericOptionResponseDTO(
        UUID id,
        String label,
        Boolean creatorChoice,
        Integer sortOrder
) {
}
