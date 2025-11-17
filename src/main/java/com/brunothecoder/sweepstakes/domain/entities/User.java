package com.brunothecoder.sweepstakes.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^\\+55\\d{10,11}$")
    @Column(nullable = false, length = 20, unique = true)
    private String whatsapp;

    @Column(nullable = false)
    private Boolean validatedUser;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}
