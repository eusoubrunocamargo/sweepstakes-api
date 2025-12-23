package com.brunothecoder.sweepstakes.application.services;

import com.brunothecoder.sweepstakes.api.dto.auth.AuthRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.auth.OTPVerificationDTO;
import com.brunothecoder.sweepstakes.api.dto.auth.TokenResponseDTO;
import com.brunothecoder.sweepstakes.domain.entities.User;
import com.brunothecoder.sweepstakes.domain.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final Map<String,String> otpStorage = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void requestOtp(AuthRequestDTO dto) {

        //generate code
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        //save in memory
        otpStorage.put(dto.whatsapp(), code);

        //send console (mock)
        System.out.println("===============");
        System.out.println("ENVIO DE SMS PARA " + dto.whatsapp());
        System.out.println("CÓDIGO OTP: " + code);
        System.out.println("===============");
    }

    @Transactional
    public TokenResponseDTO validateOtp(OTPVerificationDTO dto) {

        String storedCode = otpStorage.get(dto.whatsapp());

        if (storedCode == null || !storedCode.equals(dto.code())) {
            throw new IllegalArgumentException("Invalid/Expired Code.");
        }

        //check if is new user
        boolean isNewUser = false;

        //get user
        User user = userRepository.findByWhatsapp(dto.whatsapp()).orElse(null);

        if (user == null) {
            user = createNewUser(dto.whatsapp());
            isNewUser = true;
        }

        //remove code
        otpStorage.remove(dto.whatsapp());

        return new TokenResponseDTO("dummy-jwt-token-" + user.getId(),
                user.getId(),
                user.getName(),
                isNewUser
                );
    }

    private User createNewUser(String whatsapp) {
        User newUser = new User();
        newUser.setWhatsapp(whatsapp);
        newUser.setName("New User");
        newUser.setValidatedUser(true);
        return userRepository.save(newUser);
    }

}
