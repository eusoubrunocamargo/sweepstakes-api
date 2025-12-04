package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.pool.PoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
//import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.PoolStatus;
import com.brunothecoder.sweepstakes.domain.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PoolMapper {

    public Pool toEntity (PoolRequestDTO dto, User organizer){

        Pool pool = new Pool();

        // from BasePool
        pool.setName(dto.name());
        pool.setKeyword(dto.keyword());
        pool.setEndDate(dto.endDate());
        pool.setDrawDate(dto.drawDate());
        pool.setOrganizer(organizer);

        // from Pool
        pool.setLotteryType(dto.lotteryType());
        pool.setMinValuePerShare(dto.minValuePerShare());
        pool.setMaxValuePerShare(dto.maxValuePerShare());

        return pool;
//        return Pool.builder()
//                .name(dto.name())
//                .keyword(dto.keyword())
//                .lotteryType(dto.lotteryType())
//                .endDate(dto.endDate())
//                .drawDate(dto.drawDate())
//                .minValuePerShare(dto.minValuePerShare())
//                .maxValuePerShare(dto.maxValuePerShare())
//                .organizer(organizer)
//                .finalized(false)
//                .createdAt(LocalDateTime.now())
//                .status(PoolStatus.OPEN)
//                .build();

    }

    public PoolResponseDTO toResponse (Pool pool){
        return new PoolResponseDTO(
                pool.getId(),
                pool.getName(),
                pool.getKeyword(),
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
