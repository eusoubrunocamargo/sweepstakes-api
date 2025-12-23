package com.brunothecoder.sweepstakes.api.dto.pool_generic;

import com.brunothecoder.sweepstakes.domain.entities.LotteryType;
import com.brunothecoder.sweepstakes.domain.entities.PoolStatus;
import jdk.jshell.Snippet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GenericPoolResponseDTO(

        UUID id,
        String name,
        String description,
        String keyword,
        BigDecimal poolValue,
        LocalDateTime endDate,
        LocalDateTime drawDate,
        boolean finalized,
        UUID organizerId,
        String organizerName,
        PoolStatus status,
        LocalDateTime createdAt,
        List<GenericOptionResponseDTO> options

) {
}
