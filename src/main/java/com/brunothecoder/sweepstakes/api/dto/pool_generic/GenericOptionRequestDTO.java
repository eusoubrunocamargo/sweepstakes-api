package com.brunothecoder.sweepstakes.api.dto.pool_generic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenericOptionRequestDTO(
        @NotBlank(message = "Label is required.")
        @Size(max = 100, message = "Label must have 100 char max.")
        String label,
        Integer sortOrder,
        Boolean isCreatorChoice
) {
}
