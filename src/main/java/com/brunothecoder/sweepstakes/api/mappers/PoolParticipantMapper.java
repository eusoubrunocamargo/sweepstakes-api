package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.Player;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import org.springframework.stereotype.Component;

@Component
public class PoolParticipantMapper {

    public PoolParticipant toEntity(
            PoolParticipantRequestDTO poolParticipantRequestDTO,
            Player player,
            Pool pool){
        return PoolParticipant.builder()
                .player(player)
                .pool(pool)
                .nickname(poolParticipantRequestDTO.nickname())
                .maxValueToBet(poolParticipantRequestDTO.maxValueToBet())
                .build();
    }

    public PoolParticipantResponseDTO toResponse(PoolParticipant poolParticipant){
        return new PoolParticipantResponseDTO(
                poolParticipant.getId(),
                poolParticipant.getPlayer().getId(),
                poolParticipant.getPool().getId(),
                poolParticipant.getNickname(),
                poolParticipant.getMaxValueToBet(),
                poolParticipant.getJoinedAt()
        );
    }
}
