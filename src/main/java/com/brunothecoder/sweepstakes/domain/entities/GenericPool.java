package com.brunothecoder.sweepstakes.domain.entities;

import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "generic_pool")
public class GenericPool extends BasePool {

    private static final BigDecimal MIN_POOL_VALUE = new BigDecimal("5.00");
    private static final int MIN_OPTIONS = 2;

    @Size(max = 200)
    @Column(length = 200)
    private String description;

    @NotNull
    @DecimalMin("5.00")
    @Digits(integer = 10, fraction = 2)
    @Column(name = "pool_value", nullable = false)
    private BigDecimal poolValue;

    @OneToMany(mappedBy = "genericPool", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GenericOption> options;

    @Override
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        validatePoolValue();
        validateOptions();
    }

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

}
