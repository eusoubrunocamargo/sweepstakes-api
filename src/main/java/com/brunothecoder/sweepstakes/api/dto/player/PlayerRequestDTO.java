package com.brunothecoder.sweepstakes.api.dto.player;

import jakarta.validation.constraints.NotBlank;

public record PlayerRequestDTO(
        @NotBlank String name,
        @NotBlank String whatsapp
) {
}
