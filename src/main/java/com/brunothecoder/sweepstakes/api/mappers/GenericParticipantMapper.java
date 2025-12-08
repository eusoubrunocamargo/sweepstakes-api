package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.genericpool_participant.GenericParticipantRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.genericpool_participant.GenericParticipantResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.GenericOption;
import com.brunothecoder.sweepstakes.domain.entities.GenericPool;
import com.brunothecoder.sweepstakes.domain.entities.GenericPoolParticipant;
import com.brunothecoder.sweepstakes.domain.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GenericParticipantMapper {

    public GenericPoolParticipant toEntity(
            GenericParticipantRequestDTO dto,
            User user,
            GenericPool genericPool,
            GenericOption option){

        GenericPoolParticipant participant = new GenericPoolParticipant();

        participant.setPlayer(user);
        participant.setNickname(dto.nickname());
        participant.setGenericPool(genericPool);
        participant.setChosenOption(option);

        return participant;
    }

    public GenericParticipantResponseDTO toResponse(GenericPoolParticipant participant){
        return new GenericParticipantResponseDTO(
                participant.getId(),
                participant.getNickname(),
                participant.getPlayer().getId(),
                participant.getGenericPool().getId(),
                participant.getChosenOption().getId(),
                participant.getJoinedAt(),
                participant.getStatus()
        );
    }
}
