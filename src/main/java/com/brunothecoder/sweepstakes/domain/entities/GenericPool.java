package com.brunothecoder.sweepstakes.domain.entities;

import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericOptionRequestDTO;
import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
//@Builder
//@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
@Setter
@Entity
@Table(name = "generic_pool")
public class GenericPool extends BasePool {

//    private static final BigDecimal DEFAULT_ADMIN_FEE = new BigDecimal("0.05");
    private static final BigDecimal MIN_POOL_VALUE = new BigDecimal("5.00");
    private static final int MIN_OPTIONS = 2;


//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @NotBlank
//    @Size(max = 100)
//    @Column(nullable = false, length = 100)
//    private String name;
//
//    @NotBlank
//    @Size(max = 100)
//    @Column(nullable = false, length = 100)
//    private String keyword;

    @Size(max = 200)
    @Column(length = 200)
    private String description;

    @NotNull
    @DecimalMin("5.00")
    @Digits(integer = 10, fraction = 2)
    @Column(name = "pool_value", nullable = false)
    private BigDecimal poolValue;

//    @NotNull
//    @Column(nullable = false)
//    private LocalDateTime endDate;
//
//    @NotNull
//    @Column(nullable = false)
//    private LocalDateTime drawDate;
//
//    @Column(name = "admin_fee_percentage", precision = 5, scale = 4)
//    @Builder.Default
//    private BigDecimal adminFeePercentage = DEFAULT_ADMIN_FEE;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 20)
//    private PoolStatus status = PoolStatus.OPEN;
//
//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User organizer;
//
//    @Column(nullable = false)
//    private boolean finalized = false;
//
//    @NotNull
//    @Column(nullable = false)
//    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "genericPool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GenericOption> options;

    @Override
    @PrePersist
    protected void onCreate() {
        super.onCreate();
//        if(this.createdAt == null) {
//            this.createdAt = LocalDateTime.now();
//        }
//
//        if(this.status == null) {
//            this.status = PoolStatus.OPEN;
//        }
//
//        if(this.adminFeePercentage == null) {
//            this.adminFeePercentage = DEFAULT_ADMIN_FEE;
//        }
//
//        validateDates();
        validatePoolValue();
        validateOptions();
    }

    //private validations

//    private void validateDates() {
//
//        if(this.endDate == null || this.drawDate == null){
//            return;
//        }
//
//        if(!this.drawDate.isAfter(this.endDate)){
//            throw new IllegalStateException(ErrorMessages.POOL_INVALID_DATES);
//        }
//    }

    private void validatePoolValue() {

        if(this.poolValue == null){
            return;
        }

        if(this.poolValue.compareTo(MIN_POOL_VALUE) < 0){
            throw new IllegalStateException(ErrorMessages.POOL_INVALID_VALUE);
        }
    }

    private void validateOptions() {

        if(this.options == null || this.options.isEmpty()){
            return;
        }

        if(this.options.size() < MIN_OPTIONS){
            throw new IllegalStateException(ErrorMessages.POOL_INVALID_NUMBER_OF_OPTIONS);
        }
    }

    //state validations

//    public boolean isOpen() {
//        return PoolStatus.OPEN.equals(this.status);
//    }

//    public boolean hasExpired() {
//        return LocalDateTime.now().isAfter(this.endDate);
//    }

//    public boolean canAcceptParticipants() {
//        return isOpen() && !hasExpired();
//    }

//    public void finalize() {
//
//        if(this.finalized) {
//            throw new IllegalStateException(ErrorMessages.POOL_IS_FINALIZED);
//        }
//
//        this.finalized = true;
//
//        this.status = PoolStatus.FINALIZED;
//
//    }

    //financial methods

//    public BigDecimal calculateNetAmount(BigDecimal grossAmount) {
//
//        if(grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0){
//            return BigDecimal.ZERO;
//        }
//
//        BigDecimal netMultiplier = BigDecimal.ONE.subtract(this.adminFeePercentage);
//
//        return grossAmount.multiply(netMultiplier.setScale(2, RoundingMode.HALF_EVEN));
//    }

//    public BigDecimal calculatePlatformFee(BigDecimal grossAmount) {
//
//        if(grossAmount == null || grossAmount.compareTo(BigDecimal.ZERO) <= 0){
//            return BigDecimal.ZERO;
//        }
//
//        return grossAmount.multiply(this.adminFeePercentage).setScale(2, RoundingMode.HALF_EVEN);
//    }

//    public void validateKeyword(String providedKeyword) {
//
//        if(!this.keyword.equals(providedKeyword)){
//            throw new IllegalArgumentException(ErrorMessages.POOL_KEYWORD_INVALID);
//        }
//    }
}
