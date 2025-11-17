package com.brunothecoder.sweepstakes.domain.repositories;

import com.brunothecoder.sweepstakes.domain.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByWhatsapp(@NotBlank @Size(max = 20) @Pattern(regexp = "^\\+55\\d{10,11}$") String whatsapp);
}

