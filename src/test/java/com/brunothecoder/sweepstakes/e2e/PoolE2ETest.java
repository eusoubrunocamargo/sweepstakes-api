package com.brunothecoder.sweepstakes.e2e;

import com.brunothecoder.sweepstakes.api.dto.pool.PoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.user.UserRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.user.UserResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.LotteryType;
import com.brunothecoder.sweepstakes.domain.entities.ParticipantStatus;
import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import com.brunothecoder.sweepstakes.domain.entities.User;
import com.brunothecoder.sweepstakes.domain.repositories.PoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PoolE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PoolRepository poolRepository;

    @Autowired
    private PoolParticipantRepository poolParticipantRepository;

    private UUID organizerId;
    private UUID player1Id;
    private UUID player2Id;

    @BeforeEach
    void setUp() throws Exception {
        poolParticipantRepository.deleteAll();
        poolRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E: Fluxo completo de Pool - Criação → Participantes → Confirmação → Cálculos")
    void shouldExecuteCompletePoolFlow() throws Exception {

        // 1 - create users

        //--> organizer

        UserRequestDTO organizerRequest = new UserRequestDTO(
                "João Organizador Teste",
                "+5561992778640"
        );

        MvcResult organizerResult = mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organizerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDTO organizer = objectMapper.readValue(
                organizerResult.getResponse().getContentAsString(),
                UserResponseDTO.class
        );

        organizerId = organizer.id();

        //--> Player 1

        UserRequestDTO player1Request = new UserRequestDTO(
                "Maria do Teste",
                "+5521965716040"
        );

        MvcResult player1Result = mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player1Request)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDTO player1Response = objectMapper.readValue(
                player1Result.getResponse().getContentAsString(),
                UserResponseDTO.class
        );

        player1Id = player1Response.id();

        //--Player 2

        UserRequestDTO player2Request = new UserRequestDTO(
                "Pedro do Teste",
                "+5512999886595"
        );

        MvcResult player2Result = mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player2Request)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDTO player2Response = objectMapper.readValue(
                player2Result.getResponse().getContentAsString(),
                UserResponseDTO.class
        );

        player2Id = player2Response.id();

        // 2 - create pool

        PoolRequestDTO poolRequest = new PoolRequestDTO(
                "Bolão Mega-Sena E2E",
                "mega2026",
                LotteryType.MEGASENA,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(14),
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                organizerId,
                false,
                null
        );

        MvcResult poolResult = mockMvc.perform(post("/v1/pools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(poolRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bolão Mega-Sena E2E"))
                .andExpect(jsonPath("$.lotteryType").value("MEGASENA"))
                .andExpect(jsonPath("$.finalized").value(false))
                .andReturn();

        PoolResponseDTO pool = objectMapper.readValue(
                poolResult.getResponse().getContentAsString(),
                PoolResponseDTO.class
        );

        UUID poolId = pool.id();

        // verify pool creation

        assertThat(poolRepository.findById(poolId)).isPresent();

        // add participants

        //---> Participant 1: R$50,00

        PoolParticipantRequestDTO participant1Req = new PoolParticipantRequestDTO(
                "Maria",
                new BigDecimal("50.00"),
                player1Id,
                "mega2026"
        );

        MvcResult participant1Res = mockMvc.perform(post("/v1/pools/" + poolId + "/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(participant1Req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("Maria"))
                .andExpect(jsonPath("$.maxValueToBet").value(50.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        PoolParticipantResponseDTO participant1 = objectMapper.readValue(
                participant1Res.getResponse().getContentAsString(),
                PoolParticipantResponseDTO.class
        );

        //---> Participant 2: R$100,00

        PoolParticipantRequestDTO participant2Req = new PoolParticipantRequestDTO(
                "Pedro",
                new BigDecimal("100.00"),
                player2Id,
                "mega2026"
        );

        MvcResult participant2Res = mockMvc.perform(post("/v1/pools/" + poolId + "/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(participant2Req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("Pedro"))
                .andExpect(jsonPath("$.maxValueToBet").value(100.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        PoolParticipantResponseDTO participant2 = objectMapper.readValue(
                participant2Res.getResponse().getContentAsString(),
                PoolParticipantResponseDTO.class
        );

        // 4 - Invalid keyword (must fail)

        PoolParticipantRequestDTO invalidKeywordReq = new PoolParticipantRequestDTO(
                "Teste",
                new BigDecimal("50.00"),
                player1Id,
                "senha_errada"
        );

        mockMvc.perform(post("/v1/pools" + poolId + "/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidKeywordReq)))
                .andExpect(status().isBadRequest());

        // 5 - Invalid Bet Value (must fail)

        //---> Below min

        PoolParticipantRequestDTO belowMinReq = new PoolParticipantRequestDTO(
                "Teste",
                new BigDecimal("5.00"),
                player1Id,
                "mega2026"
        );

        mockMvc.perform(post("/v1/pools/" + poolId + "/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(belowMinReq)))
                .andExpect(status().isBadRequest());

        //---> Above max

        PoolParticipantRequestDTO aboveMaxReq = new PoolParticipantRequestDTO(
                "Teste",
                new BigDecimal("150.00"),
                player1Id,
                "mega2026"
        );

        mockMvc.perform(post("/v1/pools/" + poolId + "/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aboveMaxReq)))
                .andExpect(status().isBadRequest());

        // 6 - Confirm Payments

        //---> Confirm Participant 1

        mockMvc.perform(patch("/v1/pools/" + poolId + "/participants/" + participant1.id() + "/confirm"))
                .andExpect(status().isOk());

        //---> Confirm Participant 2

        mockMvc.perform(patch("/v1/pools" + poolId + "/participants" + participant2.id() + "/confirm"))
                .andExpect(status().isOk());

        // check confirmed status

        var confirmedParticipants = poolParticipantRepository.findAllByPoolId(poolId);
        assertThat(confirmedParticipants).hasSize(2);
        assertThat(confirmedParticipants).allMatch(p -> p.getStatus() == ParticipantStatus.CONFIRMED);

        // 7 - Calculate Game Distribution

        MvcResult distributionResult = mockMvc.perform(get("/v1/pools/" + poolId + "/game-distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poolName").value("Bolão Mega-Sena E2E"))
                .andExpect(jsonPath("$.grossAmountCollected").value(150.00))
                .andReturn();

        // 8 - Financial Calc

        String distributionJson = distributionResult.getResponse().getContentAsString();
        var distribution = objectMapper.readTree(distributionJson);

        //---> Gross amount: 50 + 100 = 150

        assertThat(distribution.get("grossAmountCollected").asDouble()).isEqualTo(150.00);

        //---> Platform Fee: 150 * 0.05 = 7.50

        assertThat(distribution.get("platformFee").asDouble()).isEqualTo(7.50);

        //---> Net Amount: 150 - 7.50 = 142.50
        assertThat(distribution.get("netAmountForBetting").asDouble()).isEqualTo(142.50);

        //---> platformFee + netAmount = gross

        double platformFee = distribution.get("platformFee").asDouble();
        double netAmount = distribution.get("netAmountForBetting").asDouble();
        double gross = distribution.get("grossAmountCollected").asDouble();

        assertThat(platformFee + netAmount).isEqualTo(gross);

        System.out.println("E2E Pool Test: SUCESSO!");
        System.out.println("   - Usuários criados: 3");
        System.out.println("   - Pool criada: " + poolId);
        System.out.println("   - Participantes: 2 confirmados");
        System.out.println("   - Validações funcionando");
        System.out.println("   - Cálculos financeiros consistentes");

    }

    @Test
    @DisplayName("E2E: Validar que mesmo usuário não pode ter pools duplicadas")
    void shouldNotAllowDuplicatePoolNameForSameUser() throws Exception {

        // Create user

        UserRequestDTO userReq = new UserRequestDTO(
                "João do Teste",
                "+5561999999999"
        );

        MvcResult userRes = mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDTO user = objectMapper.readValue(
                userRes.getResponse().getContentAsString(),
                UserResponseDTO.class
        );

        // Create first pool

        PoolRequestDTO poolReq1 = new PoolRequestDTO(
                "Bolão Mega",
                "senha123",
                LotteryType.MEGASENA,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(14),
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                user.id(),
                false,
                null
        );

        mockMvc.perform(post("/v1/pools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(poolReq1)))
                .andExpect(status().isCreated());

        // Create second pool w/ same name and same user (must fail)

        PoolRequestDTO poolReq2 = new PoolRequestDTO(
                "Bolão Mega",
                "senha456",
                LotteryType.MEGASENA,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(14),
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                user.id(),
                false,
                null
        );

        mockMvc.perform(post("/v1/pools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(poolReq2)))
                .andExpect(status().isBadRequest());

        System.out.println("Validação de pool duplicada funcionando!");
    }

    }
