package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.player.PlayerRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.player.PlayerResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public Player toEntity (PlayerRequestDTO request){
        Player player = new Player();
        player.setName(request.name());
        player.setWhatsapp(request.whatsapp());
        return player;
    }

    public PlayerResponseDTO toResponse (Player player){
        return new PlayerResponseDTO(
                player.getId().toString(),
                player.getName(),
                player.getWhatsapp(),
                player.isValidatedUser()
        );
    }
}
