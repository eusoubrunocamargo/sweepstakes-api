package com.brunothecoder.sweepstakes.api.dto.genericpool_participant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GenericParticipantRequestDTO(
        @NotBlank String nickname,
        @NotNull UUID userId,
        @NotNull UUID chosenOptionId
        ) {
}
