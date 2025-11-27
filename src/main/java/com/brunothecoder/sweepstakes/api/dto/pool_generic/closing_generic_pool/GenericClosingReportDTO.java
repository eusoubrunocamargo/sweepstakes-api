package com.brunothecoder.sweepstakes.api.dto.pool_generic.closing_generic_pool;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record GenericClosingReportDTO (
        String name,
        String description,
        String organizerName,
        BigDecimal poolValue,
        BigDecimal grossAmount,
        BigDecimal platformFee,
        BigDecimal netAmount,
        LocalDateTime endDate,
        LocalDateTime drawDate,
        LocalDateTime createdAt,
        List<OptionDTO> options,
        List<ConfirmedParticipantResultDTO> confirmedParticipants,
        List<ExpiredParticipantDTO> expiredOrCanceledParticipants
) {
}
