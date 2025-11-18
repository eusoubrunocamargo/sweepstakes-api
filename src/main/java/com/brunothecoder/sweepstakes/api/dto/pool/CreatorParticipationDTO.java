package com.brunothecoder.sweepstakes.api.dto.pool;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatorParticipationDTO(
    @Size(min = 3, max = 30)
    String nickname,

    @DecimalMin(value = "5.00", inclusive = true)
    BigDecimal maxValueToBet,

    String option
){}
