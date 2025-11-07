package com.brunothecoder.sweepstakes.api.dto.organizer;


import jakarta.validation.constraints.NotBlank;

public record CreateOrganizerRequest(
        @NotBlank String name,
        @NotBlank String whatsapp,
        @NotBlank String keyword
) {
}
