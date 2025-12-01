package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.exceptions.ErrorMessages;
import com.brunothecoder.sweepstakes.domain.entities.Role;
import com.brunothecoder.sweepstakes.domain.entities.User;
import com.brunothecoder.sweepstakes.domain.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(User user){
        //check if whatsapp already exists
        if(userRepository.existsByWhatsapp(user.getWhatsapp())){
            throw new IllegalArgumentException(ErrorMessages.USER_ALREADY_REGISTERED);
        }
        //set role player
        user.setRoles(Set.of(Role.PLAYER));
        user.setValidatedUser(false);
        return userRepository.save(user);
    }

    @Transactional
    public User addRole(UUID userId, Role role){
        //check if user exists
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND)
        );
        //add role
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public List<User>list(){
        return userRepository.findAll();
    }
}
