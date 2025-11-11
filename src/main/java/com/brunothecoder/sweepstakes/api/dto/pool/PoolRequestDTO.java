package com.brunothecoder.sweepstakes.api.dto.pool;

import com.brunothecoder.sweepstakes.domain.entities.LotteryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PoolRequestDTO (
        String name,
        LotteryType lotteryType,
        LocalDateTime endDate,
        LocalDateTime drawDate,
        BigDecimal minValuePerShare,
        BigDecimal maxValuePerShare,
        UUID organizerId
){
}
