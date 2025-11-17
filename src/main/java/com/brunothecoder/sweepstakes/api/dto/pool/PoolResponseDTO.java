package com.brunothecoder.sweepstakes.api.dto.pool;

import com.brunothecoder.sweepstakes.domain.entities.LotteryType;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PoolResponseDTO(
   UUID id,
   String name,
   String keyword,
   LotteryType lotteryType,
   LocalDateTime endDate,
   LocalDateTime drawDate,
   BigDecimal minValuePerShare,
   BigDecimal maxValuePerShare,
   boolean finalized,
   LocalDateTime createdAt,
   String organizerName
) {}
