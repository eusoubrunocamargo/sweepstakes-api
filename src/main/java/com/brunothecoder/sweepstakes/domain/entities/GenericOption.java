package com.brunothecoder.sweepstakes.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "generic_option")
public class GenericOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String label;

    @Column(name = "creator_choice")
    private Boolean creatorChoice;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "generic_pool_id", nullable = false)
    private GenericPool genericPool;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
