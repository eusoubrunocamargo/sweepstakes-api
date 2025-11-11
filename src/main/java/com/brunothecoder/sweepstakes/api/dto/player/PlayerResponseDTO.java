package com.brunothecoder.sweepstakes.api.dto.player;

import java.util.UUID;

public record PlayerResponseDTO(
        String id,
        String name,
        String whatsapp,
        Boolean validatedUser
) {
}
