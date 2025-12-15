package com.brunothecoder.sweepstakes.e2e;

import com.brunothecoder.sweepstakes.domain.entities.Role;
import com.brunothecoder.sweepstakes.domain.entities.User;
import com.brunothecoder.sweepstakes.domain.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimpleE2ETest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("E2E: Criar usuário simples")
    void shouldCreateUser() {

        // create user
        User user = new User();
        user.setName("João da Silva");
        user.setWhatsapp("+5561999999999");
        user.setValidatedUser(false);
        user.setRoles(Set.of(Role.PLAYER));

        // save
        User saved = userRepository.save(user);

        // check
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("João da Silva");
        assertThat(saved.getCreatedAt()).isNotNull();

        System.out.println("Usuário criado: " + saved.getId());
    }
}
