package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.megasena.GameDistributionResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.User;
import org.springframework.stereotype.Component;

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
    }

    public PoolResponseDTO toResponse (Pool pool, GameDistributionResponseDTO distributionResponseDTO){

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
                pool.getOrganizer().getName(),
                distributionResponseDTO
        );
    }

    public PoolResponseDTO toResponse (Pool pool){
        return toResponse(pool, null);
    }
}
