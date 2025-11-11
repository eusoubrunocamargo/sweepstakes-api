package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.organizer.OrganizerRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.organizer.OrganizerResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import org.springframework.stereotype.Component;

@Component
public class OrganizerMapper {

    public Organizer toEntity (OrganizerRequestDTO request){
        Organizer organizer = new Organizer();
        organizer.setName(request.name());
        organizer.setWhatsapp(request.whatsapp());
        organizer.setKeyword(request.keyword());
        return organizer;
    }

    public OrganizerResponseDTO toResponse (Organizer organizer){
        return new OrganizerResponseDTO(
                organizer.getId().toString(),
                organizer.getName(),
                organizer.getWhatsapp(),
                organizer.getKeyword(),
                organizer.isValidatedUser()
        );
    }
}
