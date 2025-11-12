package com.brunothecoder.sweepstakes.api.dto.megasena;

import java.math.BigDecimal;

public record GameDetailDTO (
        int numbers,
        int quantity,
        BigDecimal cost
) {
}
