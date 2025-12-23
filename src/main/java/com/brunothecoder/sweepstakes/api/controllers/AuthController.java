package com.brunothecoder.sweepstakes.api.controllers;

import com.brunothecoder.sweepstakes.api.dto.auth.AuthRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.auth.OTPVerificationDTO;
import com.brunothecoder.sweepstakes.api.dto.auth.TokenResponseDTO;
import com.brunothecoder.sweepstakes.application.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/otp/request")
    public ResponseEntity<Void> requestOtp (@RequestBody AuthRequestDTO dto) {
        authService.requestOtp(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp/validate")
    public ResponseEntity<TokenResponseDTO> validateOtp (@RequestBody OTPVerificationDTO dto) {
        return ResponseEntity.ok(authService.validateOtp(dto));
    }
}
