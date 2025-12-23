package com.brunothecoder.sweepstakes.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO (
        @NotBlank String whatsapp,
        String name
) {
}
