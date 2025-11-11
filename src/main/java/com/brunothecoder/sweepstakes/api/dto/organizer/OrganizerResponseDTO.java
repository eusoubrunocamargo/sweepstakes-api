package com.brunothecoder.sweepstakes.api.dto.organizer;

public record OrganizerResponseDTO(
        String id,
        String name,
        String whatsapp,
        String keyword,
        Boolean validatedUser
) {
}
