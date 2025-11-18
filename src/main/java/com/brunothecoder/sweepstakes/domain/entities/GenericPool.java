package com.brunothecoder.sweepstakes.domain.entities;

import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericOptionRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity(name = "pool_generic")
public class GenericPool {

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

    @Size(max = 200)
    @Column(length = 200)
    private String description;

    @NotNull
    @DecimalMin("5.00")
    @Digits(integer = 10, fraction = 2)
    @Column(name = "pool_value", nullable = false)
    private BigDecimal poolValue;

    @NotNull
    @FutureOrPresent
    @Column(nullable = false)
    private LocalDateTime endDate;

    @NotNull
    @FutureOrPresent
    @Column(nullable = false)
    private LocalDateTime drawDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User organizer;

    @Column(nullable = false)
    private boolean finalized = false;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "poolGeneric", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GenericOption> options;
}
