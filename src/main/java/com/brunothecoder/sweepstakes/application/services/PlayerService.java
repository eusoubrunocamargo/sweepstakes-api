package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.player.PlayerRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.player.PlayerResponseDTO;
import com.brunothecoder.sweepstakes.api.mappers.PlayerMapper;
import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import com.brunothecoder.sweepstakes.domain.entities.Player;
import com.brunothecoder.sweepstakes.domain.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository){
        this.playerRepository = playerRepository;
    }

    public Player create(Player player){
        return playerRepository.save(player);
    }

    public List<Player> list(){
        return playerRepository.findAll();
    }

}
