package com.brunothecoder.sweepstakes.domain.entities;

import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pool")
public class Pool extends BasePool {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LotteryType lotteryType;

    @NotNull
    @DecimalMin("5.00")
    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal minValuePerShare;

    @NotNull
    @DecimalMin("5.00")
    @Column(nullable = false)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal maxValuePerShare;

    @Override
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        validateValueRange();
    }

    private void validateValueRange() {

        if (this.minValuePerShare == null || this.maxValuePerShare == null) {
            return;
        }

        if (this.maxValuePerShare.compareTo(this.minValuePerShare) < 0) {
            throw new IllegalStateException(ErrorMessages.POOL_INVALID_RANGE);
        }

    }

    public void validateBetValue (BigDecimal betValue) {

        if (betValue == null) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_BET_VALUE);
        }

        if (betValue.compareTo(this.minValuePerShare) < 0) {
            throw new IllegalArgumentException(ErrorMessages.BELOW_POOL_MIN);
        }

        if (betValue.compareTo(this.maxValuePerShare) > 0) {
            throw new IllegalArgumentException(ErrorMessages.ABOVE_POOL_MAX);
        }
    }

}
