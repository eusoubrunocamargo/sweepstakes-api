package com.brunothecoder.sweepstakes.domain.entities;

import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class BaseParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    protected User player;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    protected String nickname;

    @NotNull
    @Column(nullable = false)
    protected LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected ParticipantStatus status = ParticipantStatus.PENDING;

    @PrePersist
    protected void onCreate() {

        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = ParticipantStatus.PENDING;
        }
    }

    public boolean isPending() {
        return ParticipantStatus.PENDING.equals(this.status);
    }

    public boolean isConfirmed() {
        return ParticipantStatus.CONFIRMED.equals(this.status);
    }

    public boolean isExpired() {
        return ParticipantStatus.EXPIRED.equals(this.status);
    }

    public void confirmPayment() {

        if (this.isConfirmed()) {
            throw new IllegalStateException(ErrorMessages.PARTICIPANT_ALREADY_CONFIRMED);
        }

        this.status = ParticipantStatus.CONFIRMED;
    }

    public void markAsExpired() {

        if (!this.isPending()) {
            throw new IllegalStateException(ErrorMessages.EXPIRED_MUST_BE_PENDING);
        }

        this.status = ParticipantStatus.EXPIRED;
    }
}
