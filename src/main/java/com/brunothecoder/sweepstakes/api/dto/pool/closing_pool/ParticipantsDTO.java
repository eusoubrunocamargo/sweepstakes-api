package com.brunothecoder.sweepstakes.api.dto.pool.closing_pool;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ParticipantsDTO(
        @NotNull List<ParticipantsSummaryDTO> confirmed,
        @NotNull List<ParticipantsSummaryDTO> canceled
) {
}
