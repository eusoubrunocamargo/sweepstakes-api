package com.brunothecoder.sweepstakes.api.dto.auth;

import java.util.UUID;

public record TokenResponseDTO(
        String token,
        UUID userId,
        String name,
        boolean isNewUser
) {
}
