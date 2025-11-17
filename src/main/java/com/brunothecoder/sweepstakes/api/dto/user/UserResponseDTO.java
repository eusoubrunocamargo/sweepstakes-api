package com.brunothecoder.sweepstakes.api.dto.user;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponseDTO (
        UUID id,
        String name,
        String whatsapp,
        Boolean validatedUser,
        Set<String> roles,
        LocalDateTime createdAt
) {
}
