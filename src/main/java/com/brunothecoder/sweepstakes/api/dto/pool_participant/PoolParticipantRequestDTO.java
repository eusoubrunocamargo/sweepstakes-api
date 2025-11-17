package com.brunothecoder.sweepstakes.api.dto.pool_participant;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record PoolParticipantRequestDTO(
        @NotBlank @Size(max = 50) String nickname,
        @NotNull @DecimalMin("5.00") BigDecimal maxValueToBet,
        UUID userId,
        @NotBlank @Size(max = 50) String keyword
) {
}
