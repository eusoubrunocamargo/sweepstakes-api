package com.brunothecoder.sweepstakes.domain.entities;

import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import io.lettuce.core.support.BasePool;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "pool")
public class Pool {

    private static final BigDecimal DEFAULT_ADMIN_FEE = new BigDecimal("0.05");

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String keyword;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LotteryType lotteryType;

    @NotNull
    //@FutureOrPresent
    @Column(nullable = false)
    private LocalDateTime endDate;

    @NotNull
    //@FutureOrPresent
    @Column(nullable = false)
    private LocalDateTime drawDate;

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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User organizer;

    @Column(nullable = false)
    boolean finalized;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "admin_fee_percentage", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal adminFeePercentage = new BigDecimal("0.05");

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PoolStatus status = PoolStatus.OPEN;

    @PrePersist
    protected void onCreate() {

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = PoolStatus.OPEN;
        }

        if (this.adminFeePercentage == null) {
            this.adminFeePercentage = DEFAULT_ADMIN_FEE;
        }

        validateDates();
        validateValueRange();
    }

    private void validateDates() {

        if (this.endDate == null || this.drawDate == null) {
            return;
        }

        if(!this.drawDate.isAfter(this.endDate)) {
            throw new IllegalStateException(ErrorMessages.POOL_INVALID_DATES);
        }
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

    public void validateKeyword (String providedKeyword) {

        if (!this.keyword.equals(providedKeyword)) {
            throw new IllegalArgumentException(ErrorMessages.POOL_KEYWORD_INVALID);
        }
    }

    public boolean isOpen() {
        return PoolStatus.OPEN.equals(this.status) && !this.finalized;
    }

    public boolean hasExpired() {
        return LocalDateTime.now().isAfter(this.endDate);
    }

    public boolean canAcceptParticipants() {
        return isOpen() && !hasExpired();
    }

    public void finalizePool() {

        if (this.finalized) {
            throw new IllegalStateException(ErrorMessages.POOL_IS_FINALIZED);
        }

        this.finalized = true;

        this.status = PoolStatus.FINALIZED;
    }

    public BigDecimal calculateNetAmount(BigDecimal grossAmount) {

        if (grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0){
            return BigDecimal.ZERO;
        }

        BigDecimal netMultiplier = BigDecimal.ONE.subtract(this.adminFeePercentage);
        return grossAmount.multiply(netMultiplier).setScale(2, RoundingMode.HALF_EVEN);

    }

    public BigDecimal calculatePlatformFee(BigDecimal grossAmount) {

        if(grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0){
            return BigDecimal.ZERO;
        }

        return grossAmount.multiply(this.adminFeePercentage).setScale(2, RoundingMode.HALF_EVEN);
    }

}
