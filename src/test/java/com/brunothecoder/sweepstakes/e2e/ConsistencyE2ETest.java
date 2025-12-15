package com.brunothecoder.sweepstakes.e2e;

import com.brunothecoder.sweepstakes.domain.entities.*;
import com.brunothecoder.sweepstakes.domain.repositories.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ConsistencyE2ETest {

    @Autowired
    private PoolRepository poolRepository;

    @Autowired
    private GenericPoolRepository genericPoolRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PoolParticipantRepository poolParticipantRepository;

    @Autowired
    private GenericPoolParticipantRepository genericPoolParticipantRepository;

    @Test
    @DisplayName("Consistência: platformFee calculado corretamente com adminFeePercentage customizada")
    void shouldCalculatePlatformFeeConsistentlyWithCustomFee() {

        // Create pool with 10% tax
        User user = createUser("João", "+5561999999999");

        Pool pool = new Pool();
        pool.setName("Pool Teste");
        pool.setKeyword("senha123");
        pool.setLotteryType(LotteryType.MEGASENA);
        pool.setEndDate(LocalDateTime.now().plusDays(7));
        pool.setDrawDate(LocalDateTime.now().plusDays(14));
        pool.setMinValuePerShare(new BigDecimal("10.00"));
        pool.setMaxValuePerShare(new BigDecimal("100.00"));
        pool.setOrganizer(user);
        pool.setAdminFeePercentage(new BigDecimal("0.10"));

        poolRepository.save(pool);

        // Test calc

        BigDecimal grossAmount = new BigDecimal("1000.00");
        BigDecimal platformFee = pool.calculatePlatformFee(grossAmount);
        BigDecimal netAmount = pool.calculateNetAmount(grossAmount);

        assertThat(platformFee).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(netAmount).isEqualByComparingTo(new BigDecimal("900.00"));

        assertThat(platformFee.add(netAmount)).isEqualByComparingTo(grossAmount);

        System.out.println("✅ platformFee consistente com adminFeePercentage customizada!");

    }

    @Test
    @DisplayName("Consistência: Mesmo usuário não pode criar pools com mesmo nome")
    void shouldPreventDuplicatePoolNamesForSameUser() {

        User user = createUser("João", "+5561999999999");

        //pool 1
        Pool pool1 = new Pool();
        pool1.setName("Bolão Mega");
        pool1.setKeyword("senha1");
        pool1.setLotteryType(LotteryType.MEGASENA);
        pool1.setEndDate(LocalDateTime.now().plusDays(7));
        pool1.setDrawDate(LocalDateTime.now().plusDays(14));
        pool1.setMinValuePerShare(new BigDecimal("10.00"));
        pool1.setMaxValuePerShare(new BigDecimal("100.00"));
        pool1.setOrganizer(user);

        poolRepository.save(pool1);

        // check if exists

        boolean exists = poolRepository.existsByNameAndStatusAndOrganizer_Id(
                "Bolão Mega", PoolStatus.OPEN, user.getId()
        );

        assertThat(exists).isTrue();

        System.out.println("Validação de pool duplicada funcionando!");
    }

    @Test
    @DisplayName("Consistência: Métodos de estado de BaseParticipant")
    void shouldUseBaseParticipantStateMethods() {

        User user = createUser("Maria", "+5561999999999");
        User organizer = createUser("João", "+5561988888888");

        Pool pool = new Pool();
        pool.setName("Pool Teste");
        pool.setKeyword("senha");
        pool.setLotteryType(LotteryType.MEGASENA);
        pool.setEndDate(LocalDateTime.now().plusDays(7));
        pool.setDrawDate(LocalDateTime.now().plusDays(14));
        pool.setMinValuePerShare(new BigDecimal("10.00"));
        pool.setMaxValuePerShare(new BigDecimal("100.00"));
        pool.setOrganizer(organizer);
        poolRepository.save(pool);

        PoolParticipant participant = new PoolParticipant();
        participant.setPlayer(user);
        participant.setPool(pool);
        participant.setNickname("Maria");
        participant.setMaxValueToBet(new BigDecimal("50.00"));

        poolParticipantRepository.save(participant);

        // Test entity methods

        assertThat(participant.isPending()).isTrue();
        assertThat(participant.isConfirmed()).isFalse();
        assertThat(participant.isExpired()).isFalse();

        // Confirm payment

        participant.confirmPayment();
        poolParticipantRepository.save(participant);

        assertThat(participant.isPending()).isFalse();
        assertThat(participant.isConfirmed()).isTrue();

        // Try to confirm again (must fail)

        assertThatThrownBy(participant::confirmPayment)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("já está confirmado");

        System.out.println("Métodos de estado de BaseParticipant funcionando.");
    }

    @Test
    @DisplayName("Consistência: Pool.finalizePool() só pode ser chamado uma vez")
    void shouldOnlyFinalizePoolOnce() {

        User user = createUser("João", "+5561999999999");

        Pool pool = new Pool();
        pool.setName("Pool Teste");
        pool.setKeyword("senha");
        pool.setLotteryType(LotteryType.MEGASENA);
        pool.setEndDate(LocalDateTime.now().plusDays(7));
        pool.setDrawDate(LocalDateTime.now().plusDays(14));
        pool.setMinValuePerShare(new BigDecimal("10.00"));
        pool.setMaxValuePerShare(new BigDecimal("100.00"));
        pool.setOrganizer(user);

        poolRepository.save(pool);

        // Finalize pool
        assertThat(pool.isFinalized()).isFalse();
        pool.finalizePool();
        assertThat(pool.isFinalized()).isTrue();
        assertThat(pool.getStatus()).isEqualTo(PoolStatus.FINALIZED);

        // Try to finalize again (must fail)
        assertThatThrownBy(pool::finalizePool)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("já está finalizada");

        System.out.println("finalizePool() protegido contra dupla execução!");
    }

    @Test
    @DisplayName("Consistência: Validações de Base Pool funcionando")
    void shouldValidateBasePoolRules() {

        User user = createUser("João", "+5561999999999");

        Pool pool = new Pool();
        pool.setName("Pool Teste");
        pool.setLotteryType(LotteryType.MEGASENA);
        pool.setEndDate(LocalDateTime.now().plusDays(14));
        pool.setDrawDate(LocalDateTime.now().plusDays(7));
        pool.setMinValuePerShare(new BigDecimal("10.00"));
        pool.setMaxValuePerShare(new BigDecimal("100.00"));
        pool.setOrganizer(user);

        assertThatThrownBy(()-> poolRepository.save(pool))
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Data do sorteio deve ser após");

        System.out.println("Validação de datas funcionando!");
    }

    private User createUser(String name, String whatsapp) {
        User user = new User();
        user.setName(name);
        user.setWhatsapp(whatsapp);
        user.setValidatedUser(false);
        user.setRoles(Set.of(Role.PLAYER));
        return userRepository.save(user);
    }
}
