package com.brunothecoder.sweepstakes.api.mappers;

import com.brunothecoder.sweepstakes.api.dto.user.UserRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.user.UserResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO request){
        return User.builder()
                .name(request.name())
                .whatsapp(request.whatsapp())
                .validatedUser(false)
                .build();
    }

    public UserResponseDTO toResponse(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getWhatsapp(),
                user.getValidatedUser(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                user.getCreatedAt()
        );
    }
}
