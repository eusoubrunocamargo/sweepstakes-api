package com.brunothecoder.sweepstakes.e2e;

import com.brunothecoder.sweepstakes.api.dto.genericpool_participant.GenericParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericOptionRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.user.UserRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.user.UserResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.ParticipantStatus;
import com.brunothecoder.sweepstakes.domain.repositories.GenericPoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.GenericPoolRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GenericPoolE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenericPoolRepository genericPoolRepository;

    @Autowired
    private GenericPoolParticipantRepository genericPoolParticipantRepository;

    @BeforeEach
    void setUp() {
        genericPoolRepository.deleteAll();;
        genericPoolParticipantRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E: Fluxo completo de GenericPool com cálculo correto de platformFee")
    void shouldExecuteCompleteGenericPoolFlowWithCorrectFee() throws Exception {

        // 1 - create users

    UserRequestDTO organizer = new UserRequestDTO(
            "João do Teste",
            "+5561999886595"
    );

    MvcResult organizerResult = mockMvc.perform(post("/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(organizer)))
            .andExpect(status().isCreated())
            .andReturn();

        UserResponseDTO organizerRes = objectMapper.readValue(
                organizerResult.getResponse().getContentAsString(),
                UserResponseDTO.class
        );

        UserRequestDTO player1 = new UserRequestDTO(
                "Maria do Teste",
                "+5561999999999"
        );

        MvcResult player1Result = mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player1)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDTO player1Res = objectMapper.readValue(
                player1Result.getResponse().getContentAsString(),
                UserResponseDTO.class
        );

        UserRequestDTO player2 = new UserRequestDTO("Pedro do Teste", "+5561910101010");
        MvcResult player2Result = mockMvc.perform(post("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player2)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDTO player2Res = objectMapper.readValue(
                player2Result.getResponse().getContentAsString(),
                UserResponseDTO.class
        );

        // 2 - Create Generic Pool with 10% platform fee

        GenericPoolRequestDTO poolReq = new GenericPoolRequestDTO(
                "Bolão da Copa E2E",
                "copa2026",
                "Quem vence a Copa?",
                new BigDecimal("25.00"),
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(14),
                organizerRes.id(),
                List.of(
                        new GenericOptionRequestDTO("Brasil", 1, false),
                        new GenericOptionRequestDTO("Argentina", 2, false),
                        new GenericOptionRequestDTO("França", 3, true)
                ), true, null);

        MvcResult poolResult = mockMvc.perform(post("/v1/pools/generic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(poolReq)))
                .andExpect(status().isCreated())
                .andReturn();

        GenericPoolResponseDTO pool = objectMapper.readValue(
                poolResult.getResponse().getContentAsString(),
                GenericPoolResponseDTO.class
        );

        UUID poolId = pool.id();

        UUID francaOptionId = pool.options().stream()
                .filter(opt -> opt.label().equals("França"))
                .findFirst().get().id();

        UUID brasilOptionId = pool.options().stream()
                .filter(opt -> opt.label().equals("Brasil"))
                .findFirst().get().id();


        UUID argentinaOptionId = pool.options().stream()
                .filter(opt -> opt.label().equals("Argentina"))
                .findFirst().get().id();

        // Check creator as participant

        var initialParticipants = genericPoolParticipantRepository.findAllByGenericPool_Id(poolId);
        assertThat(initialParticipants).hasSize(1);

        // Add Participants

        //---> player 1 picks Brasil

        GenericParticipantRequestDTO participant1Req = new GenericParticipantRequestDTO(
                "Maria",
                player1Res.id(),
                brasilOptionId
        );

        mockMvc.perform(post("/v1/pools/generic/" + poolId + "/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(participant1Req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("Maria"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        //---> player 2 picks Argentina

        GenericParticipantRequestDTO participant2Req = new GenericParticipantRequestDTO(
                "Pedro",
                player2Res.id(),
                argentinaOptionId
        );

        mockMvc.perform(post("/v1/pools/generic/" + poolId + "/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(participant2Req)))
                .andExpect(status().isCreated());

        // Verify number of participants

        var allParticipants = genericPoolParticipantRepository.findAllByGenericPool_Id(poolId);
        assertThat(allParticipants).hasSize(3);

        // 5 - Simulate payment confirmation

        //---> manual confirmation

        allParticipants.forEach(p -> {
            p.confirmPayment();
            genericPoolParticipantRepository.save(p);
        });

        //---> verify all CONFIRMED

        var confirmedParticipants = genericPoolParticipantRepository.findAllByGenericPool_Id(poolId);
        assertThat(confirmedParticipants).allMatch(
                p -> p.getStatus() == ParticipantStatus.CONFIRMED);

        // 6 - Get Pool and Check Financial Calc

        var savedPool = genericPoolRepository.findById(poolId).orElseThrow();

        // Gross amount: 3 participants * R$25 = R$75

        BigDecimal grossAmount = new BigDecimal("75.00");

        // Check entity methods

        BigDecimal platformFee = savedPool.calculatePlatformFee(grossAmount);
        BigDecimal netAmount = savedPool.calculateNetAmount(grossAmount);

        // Check calc: platformFee = 75 * 0.05 = 3.75 && netAmount = 75 - 3.75 = 71.25

        assertThat(platformFee).isEqualByComparingTo(new BigDecimal("3.75"));
        assertThat(netAmount).isEqualByComparingTo(new BigDecimal("71.25"));

        // platformFee + netAmount === grossAmount

        BigDecimal sum = platformFee.add(netAmount);
        assertThat(sum).isEqualByComparingTo(grossAmount);

        System.out.println("E2E GenericPool Test: SUCESSO");
        System.out.println("   - Usuários criados: 3");
        System.out.println("   - Participantes: 3 confirmados");
        System.out.println("   - Gross: " + grossAmount);
        System.out.println("   - Platform Fee: " + platformFee);
        System.out.println("   - Net Amount: " + netAmount);
        System.out.println("   - Consistência: " + platformFee + " + " + netAmount + " = " + sum);

    }
}
