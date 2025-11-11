package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.pool.PoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PoolMapper {

    public Pool toEntity (PoolRequestDTO dto, Organizer organizer){
        return Pool.builder()
                .name(dto.name())
                .lotteryType(dto.lotteryType())
                .endDate(dto.endDate())
                .drawDate(dto.drawDate())
                .minValuePerShare(dto.minValuePerShare())
                .maxValuePerShare(dto.maxValuePerShare())
                .organizer(organizer)
                .finalized(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public PoolResponseDTO toResponse (Pool pool){
        return new PoolResponseDTO(
                pool.getId(),
                pool.getName(),
                pool.getLotteryType(),
                pool.getEndDate(),
                pool.getDrawDate(),
                pool.getMinValuePerShare(),
                pool.getMaxValuePerShare(),
                pool.isFinalized(),
                pool.getCreatedAt(),
                pool.getOrganizer().getName()
        );
    }
}
