package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.megasena.GameDistributionResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.closing_pool.ClosingReportDTO;
import com.brunothecoder.sweepstakes.application.services.calculators.ParticipantShareCalculator;
import com.brunothecoder.sweepstakes.domain.entities.ParticipantStatus;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import com.brunothecoder.sweepstakes.domain.entities.PoolStatus;
import com.brunothecoder.sweepstakes.domain.repositories.PoolParticipantRepository;
import com.brunothecoder.sweepstakes.domain.repositories.PoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PoolClosingServiceTest {

    @Mock
    private PoolRepository poolRepository;
    @Mock
    private PoolParticipantRepository poolParticipantRepository;
    @Mock
    private FinancialService financialService;
    @Mock
    private PoolService poolService;

    private PoolClosingService poolClosingService;

    @BeforeEach
    void setUp() {
        ParticipantShareCalculator participantShareCalculator = new ParticipantShareCalculator();
        poolClosingService = new PoolClosingService(
                poolRepository,
                poolParticipantRepository,
                financialService,
                poolService,
                participantShareCalculator
        );
    }

    @Test
    void shouldCloseExpirePoolsAndGenerateReports(){

        //arrange
        Pool pool1 = new Pool();
        pool1.setId(UUID.randomUUID());
        pool1.setStatus(PoolStatus.OPEN);

        Pool pool2 = new Pool();
        pool2.setId(UUID.randomUUID());
        pool2.setStatus(PoolStatus.OPEN);

        List<Pool> expiredPools = List.of(pool1, pool2);

        when(poolRepository.findAllExpiredPools(any())).thenReturn(expiredPools);
        when(poolRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(poolRepository.findById(any()))
                .thenReturn(Optional.of(pool1))
                .thenReturn(Optional.of(pool2));
        when(poolService.calculateGameDistribution(any())).thenReturn(mock(GameDistributionResponseDTO.class));
        when(poolParticipantRepository.getConfirmedTotalAmount(any())).thenReturn(BigDecimal.valueOf(100));
        when(financialService.calculateNetAmountForBetting(any(), any())).thenReturn(BigDecimal.valueOf(95));
        when(poolParticipantRepository.findAllByPoolId(any())).thenReturn(Collections.emptyList());

        //act
        poolClosingService.closeExpiredPools();

        //assert
        verify(poolRepository, times(2)).save(any(Pool.class));
        assertEquals(PoolStatus.FINALIZED, pool1.getStatus());
        assertEquals(PoolStatus.FINALIZED, pool2.getStatus());
        verify(poolService, times(2)).calculateGameDistribution(any());
    }

    @Test
    void shouldFinalizePoolAndGenerateReport(){

        //arrange

        Pool pool = new Pool();
        pool.setId(UUID.randomUUID());
        pool.setStatus(PoolStatus.OPEN);

        when(poolRepository.save(any())).thenReturn(pool);
        when(poolRepository.findById(pool.getId())).thenReturn(Optional.of(pool));
        when(poolService.calculateGameDistribution(pool.getId()))
                .thenReturn(mock(GameDistributionResponseDTO.class));

        //act

        poolClosingService.processPoolClosure(pool);

        //assert
        assertEquals(PoolStatus.FINALIZED, pool.getStatus());
        verify(poolRepository).save(pool);
        verify(poolService).calculateGameDistribution(pool.getId());

    }

    @Test
    void shouldGenerateClosingReportCorrectly() {

        //arrange
        UUID poolId = UUID.randomUUID();
        Pool pool = new Pool();
        pool.setId(poolId);
        pool.setName("Bolão Teste");
        pool.setAdminFeePercentage(BigDecimal.valueOf(0.05));

        PoolParticipant confirmed = new PoolParticipant();
        confirmed.setNickname("Alice");
        confirmed.setMaxValueToBet(BigDecimal.valueOf(100));
        confirmed.setStatus(ParticipantStatus.CONFIRMED);

        PoolParticipant canceled = new PoolParticipant();
        canceled.setNickname("Bob");
        canceled.setMaxValueToBet(BigDecimal.valueOf(50));
        canceled.setStatus(ParticipantStatus.EXPIRED);

        when(poolRepository.findById(poolId)).thenReturn(Optional.of(pool));

        when(poolParticipantRepository.getConfirmedTotalAmount(poolId)).thenReturn(BigDecimal.valueOf(100));


        when(financialService.calculateNetAmountForBetting(BigDecimal.valueOf(100), pool.getAdminFeePercentage()))
                .thenReturn(BigDecimal.valueOf(95));

        when(poolService.calculateGameDistribution(poolId)).thenReturn(mock(GameDistributionResponseDTO.class));

        when(poolParticipantRepository.findAllByPoolId(poolId)).thenReturn(List.of(confirmed, canceled));

        //act
        ClosingReportDTO report = poolClosingService.generateClosingReport(poolId);

        //assert
        assertEquals("Bolão Teste", report.poolName());
        assertEquals(BigDecimal.valueOf(100), report.grossAmountCollected());
        assertEquals(BigDecimal.valueOf(95), report.netAmountForBetting());
        assertEquals(1, report.participants().confirmed().size());
        assertEquals(1, report.participants().canceled().size());
        assertEquals(100, report.participants().confirmed().get(0).percentOfPrize(), 0.01);

    }
}
