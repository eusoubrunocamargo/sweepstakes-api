package com.brunothecoder.sweepstakes.api.dto.pool;

import com.brunothecoder.sweepstakes.domain.entities.LotteryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PoolRequestDTO (
        @NotBlank(message = "Pool's name is required.") @Size(max = 100, message = "Pool name must have max 100 chars.") String name,
        @NotBlank @Size(max = 50) String keyword,
        LotteryType lotteryType,
        @NotNull(message = "End date is required.") @FutureOrPresent LocalDateTime endDate,
        @NotNull(message = "Draw date is required.") @FutureOrPresent LocalDateTime drawDate,
        @NotNull(message = "Min value is required") @DecimalMin(value = "5.00", inclusive = true, message = "Min value is 5.00") BigDecimal minValuePerShare,
        @NotNull(message = "Max value is required.") @DecimalMin(value = "5.00") BigDecimal maxValuePerShare,
        @NotNull(message = "User ID is required.") UUID userId,
        Boolean includeCreatorAsParticipant,
        @Valid CreatorParticipationDTO creatorParticipation
){
}
