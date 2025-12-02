package com.brunothecoder.sweepstakes.domain.entities;

import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
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
        validate();
    }

    public boolean addRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        return this.roles.add(role);
    }

    public boolean promoteToOrganizer() {
        return addRole(Role.ORGANIZER);
    }

    public boolean hasRole(Role role) {
        return this.roles != null && this.roles.contains(role);
    }

    public boolean isOrganizer() {
        return hasRole(Role.ORGANIZER);
    }

    public boolean hasFullName() {

        if(this.name == null || this.name.trim().isEmpty()) {
            return false;
        }

        String[] parts = name.trim().split("\\s+");

        if(parts.length < 2) {
            return false;
        }

        for(String part: parts) {
            if (part.length() < 2) {
                return false;
            }
        }
        return true;
    }

    public boolean hasValidWhatsAppFormat() {
        if(this.whatsapp == null) {
            return true;
        }

        String pattern = "^\\+55\\d{10,11}$";
        return !this.whatsapp.matches(pattern);
    }

    public String getWhatsAppDDD() {
        if(hasValidWhatsAppFormat()){
            return null;
        }
        return this.whatsapp.substring(3,5);
    }

    public void validate() {

        if(!hasFullName()) {
            throw new IllegalStateException(ErrorMessages.USER_INVALID_NAME);
        }

        if(hasValidWhatsAppFormat()) {
            throw new IllegalStateException(ErrorMessages.USER_INVALID_WHATSAPP);
        }
    }

}
