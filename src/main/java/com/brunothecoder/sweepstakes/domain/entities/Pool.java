package com.brunothecoder.sweepstakes.domain.entities;

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
@Entity(name = "pool")
public class Pool {

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
    @FutureOrPresent
    @Column(nullable = false)
    private LocalDateTime endDate;

    @NotNull
    @FutureOrPresent
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
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User organizer;

    @Column(nullable = false)
    boolean finalized;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

}
