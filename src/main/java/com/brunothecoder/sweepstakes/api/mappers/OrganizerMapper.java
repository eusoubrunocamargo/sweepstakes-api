package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.organizer.CreateOrganizerRequest;
import com.brunothecoder.sweepstakes.api.dto.organizer.OrganizerResponse;
import com.brunothecoder.sweepstakes.domain.entities.Organizer;
import org.springframework.stereotype.Component;

@Component
public class OrganizerMapper {

    public Organizer toEntity (CreateOrganizerRequest request){
        Organizer organizer = new Organizer();
        organizer.setName(request.name());
        organizer.setWhatsapp(request.whatsapp());
        organizer.setKeyword(request.keyword());
        return organizer;
    }

    public OrganizerResponse toResponse (Organizer organizer){
        return new OrganizerResponse(
                organizer.getId().toString(),
                organizer.getName(),
                organizer.getWhatsapp(),
                organizer.getKeyword()
        );
    }
}
