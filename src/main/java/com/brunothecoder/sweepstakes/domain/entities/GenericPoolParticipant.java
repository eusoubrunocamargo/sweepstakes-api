package com.brunothecoder.sweepstakes.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "generic_pool_id"})
}, name = "generic_participant")
public class GenericPoolParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User player;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "generic_pool_id", nullable = false)
    private GenericPool genericPool;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "chosen_option_id", nullable = false)
    private GenericOption chosenOption;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String nickname;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipantStatus status = ParticipantStatus.PENDING;

}
