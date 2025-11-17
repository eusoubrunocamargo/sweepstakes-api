package com.brunothecoder.sweepstakes.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity(name = "pool_participant")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "pool_id"}),
        @UniqueConstraint(columnNames = {"nickname", "pool_id"})
})
public class PoolParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User player;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "pool_id", nullable = false)
    private Pool pool;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String nickname;

    @DecimalMin("5.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal maxValueToBet;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime joinedAt;

}
