package com.brunothecoder.sweepstakes.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 20)
        @Pattern(regexp = "^\\+55\\d{11}$")
        String whatsapp
) {
}
