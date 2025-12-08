package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_participant.PoolParticipantResponseDTO;
//import com.brunothecoder.sweepstakes.domain.entities.Player;
import com.brunothecoder.sweepstakes.domain.entities.Pool;
import com.brunothecoder.sweepstakes.domain.entities.PoolParticipant;
import com.brunothecoder.sweepstakes.domain.entities.User;
import org.springframework.stereotype.Component;

@Component
public class PoolParticipantMapper {

    public PoolParticipant toEntity(
            PoolParticipantRequestDTO poolParticipantRequestDTO,
            User user,
            Pool pool){

        PoolParticipant participant = new PoolParticipant();

        participant.setPlayer(user);
        participant.setNickname(poolParticipantRequestDTO.nickname());
        participant.setPool(pool);
        participant.setMaxValueToBet(poolParticipantRequestDTO.maxValueToBet());

        return participant;
    }

    public PoolParticipantResponseDTO toResponse(PoolParticipant poolParticipant){
        return new PoolParticipantResponseDTO(
                poolParticipant.getId(),
                poolParticipant.getPlayer().getId(),
                poolParticipant.getPool().getId(),
                poolParticipant.getNickname(),
                poolParticipant.getMaxValueToBet(),
                poolParticipant.getStatus(),
                poolParticipant.getJoinedAt()
        );
    }
}
