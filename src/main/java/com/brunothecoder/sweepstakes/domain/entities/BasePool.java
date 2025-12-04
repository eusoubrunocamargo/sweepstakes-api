package com.brunothecoder.sweepstakes.domain.entities;

import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public abstract class BasePool {

    protected static final BigDecimal DEFAULT_ADMIN_FEE = new BigDecimal("0.05");

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    protected String name;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    protected String keyword;

    @NotNull
    @Column(nullable = false)
    protected LocalDateTime endDate;

    @NotNull
    @Column(nullable = false)
    protected LocalDateTime drawDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    protected User organizer;

    @Column(nullable = false)
    protected boolean finalized = false;

    @NotNull
    @Column(nullable = false)
    protected LocalDateTime createdAt;

    @Column(name = "admin_fee_percentage", precision = 5, scale = 4)
    protected BigDecimal adminFeePercentage = DEFAULT_ADMIN_FEE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected PoolStatus status = PoolStatus.OPEN;

    // lifecycle callback

    @PrePersist
    protected void onCreate() {

        if(this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if(this.status == null) {
            this.status = PoolStatus.OPEN;
        }

        if(this.adminFeePercentage == null) {
            this.adminFeePercentage = DEFAULT_ADMIN_FEE;
        }

        validateDates();
    }

    // validations

    protected void validateDates() {

        if(this.endDate == null || this.drawDate == null) {
            return;
        }

        if(!this.drawDate.isAfter(this.endDate)) {
            throw new IllegalStateException(ErrorMessages.POOL_INVALID_DATES);
        }
    }

    public void validateKeyword(String providedKeyword) {

        if(!this.keyword.equals(providedKeyword)) {
            throw new IllegalArgumentException(ErrorMessages.POOL_KEYWORD_INVALID);
        }
    }

    //state method

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

        if(this.finalized) {
            throw new IllegalArgumentException(ErrorMessages.POOL_IS_FINALIZED);
        }

        this.finalized = true;

        this.status = PoolStatus.FINALIZED;
    }

    // financial methods

    public BigDecimal calculateNetAmount(BigDecimal grossAmount) {

        if(grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal netMultiplier = BigDecimal.ONE.subtract(this.adminFeePercentage);

        return grossAmount.multiply(netMultiplier).setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal calculatePlatformFee(BigDecimal grossAmount) {

        if(grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return grossAmount.multiply(this.adminFeePercentage).setScale(2, RoundingMode.HALF_EVEN);
    }
}
