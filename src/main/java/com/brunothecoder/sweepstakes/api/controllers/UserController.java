package com.brunothecoder.sweepstakes.api.controllers;

import com.brunothecoder.sweepstakes.api.dto.user.UserRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.user.UserResponseDTO;
import com.brunothecoder.sweepstakes.api.mappers.UserMapper;
import com.brunothecoder.sweepstakes.application.services.UserService;
import com.brunothecoder.sweepstakes.domain.entities.Role;
import com.brunothecoder.sweepstakes.domain.entities.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper){
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO>create(@Valid @RequestBody UserRequestDTO userRequestDTO){
        User saved = userService.registerUser(userMapper.toEntity(userRequestDTO));
        return ResponseEntity.created(URI.create(
                        "/v1/users/" + saved.getId()))
                .body(userMapper.toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>>list(){
        return ResponseEntity.ok(userService
                .list()
                .stream()
                .map(userMapper::toResponse)
                .toList());
    }

    @PatchMapping("/{id}/roles")
    public ResponseEntity<UserResponseDTO>addRole(@PathVariable UUID id, @RequestParam Role role){
        User updated = userService.addRole(id, role);
        return ResponseEntity.ok(userMapper.toResponse(updated));
    }
}
