package com.brunothecoder.sweepstakes.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record OTPVerificationDTO (
        @NotBlank String whatsapp,
        @NotBlank String code
) {
}
