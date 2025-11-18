package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericOptionRequestDTO;
import com.brunothecoder.sweepstakes.domain.entities.GenericOption;
import com.brunothecoder.sweepstakes.domain.entities.GenericPool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GenericOptionMapper {

    public GenericOption toEntity(GenericOptionRequestDTO dto, GenericPool genericPool){
        return GenericOption.builder()
                .label(dto.label())
                .creatorChoice(dto.isCreatorChoice())
                .sortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0)
                .poolGeneric(genericPool)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
