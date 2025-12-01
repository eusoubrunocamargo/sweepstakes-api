package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericOptionRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericOptionResponseDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.GenericOption;
import com.brunothecoder.sweepstakes.domain.entities.GenericPool;
import com.brunothecoder.sweepstakes.domain.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GenericPoolMapper {

    public GenericPool toEntity(GenericPoolRequestDTO dto, User organizer){

        return GenericPool.builder()
                .name(dto.name())
                .keyword(dto.keyword())
                .description(dto.description())
                .poolValue(dto.poolValue())
                .endDate(dto.endDate())
                .drawDate(dto.drawDate())
                .organizer(organizer)
                .finalized(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public GenericPoolResponseDTO toResponse(GenericPool genericPool, List<GenericOption> options){

        List<GenericOptionResponseDTO> optionResponses = options
                .stream()
                .map(opt -> new GenericOptionResponseDTO(
                        opt.getId(),
                        opt.getLabel(),
                        opt.getCreatorChoice(),
                        opt.getSortOrder()
                )).toList();

        return new GenericPoolResponseDTO(
                        genericPool.getId(),
                        genericPool.getName(),
                        genericPool.getDescription(),
                        genericPool.getKeyword(),
                        genericPool.getPoolValue(),
                        genericPool.getEndDate(),
                        genericPool.getDrawDate(),
                        genericPool.isFinalized(),
                        genericPool.getOrganizer().getId(),
                        genericPool.getCreatedAt(),
                        optionResponses
                );
    }
}
