package com.brunothecoder.sweepstakes.api.dto.pool_generic;

import com.brunothecoder.sweepstakes.api.dto.pool.CreatorParticipationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GenericPoolRequestDTO(

        @NotBlank(message = "Pool's name is required.")
        @Size(max = 100, message = "Pool name must have max 100 chars.")
        String name,

        @NotBlank
        @Size(max = 50, message = "Keyword must have max 100 chars.")
        String keyword,

        @NotBlank
        @Size(max = 200, message = "Description must have max 200 chars.")
        String description,

        @NotNull(message = "Value is required.")
        @DecimalMin(value = "5.00", inclusive = true, message = "Min value is 5.00")
        BigDecimal poolValue,

        @NotNull(message = "End date is required.")
        @FutureOrPresent
        LocalDateTime endDate,

        @NotNull(message = "Draw date is required.")
        @FutureOrPresent
        LocalDateTime drawDate,

        @NotNull(message = "User ID is required.")
        UUID userId,

        @Size(min = 2, message = "Pool must have at least 2 options.")
        @Valid
        List<GenericOptionRequestDTO> options,

        Boolean includeCreatorAsParticipant,

        @Valid
        CreatorParticipationDTO creatorParticipation
) {
}
