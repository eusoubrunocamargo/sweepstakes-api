package com.brunothecoder.sweepstakes.api.controllers;

import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool_generic.GenericPoolResponseDTO;
import com.brunothecoder.sweepstakes.application.services.GenericPoolService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/pools/generic")
public class GenericPoolController {

    private final GenericPoolService genericPoolService;

    public GenericPoolController(GenericPoolService genericPoolService){
        this.genericPoolService = genericPoolService;
    }

    @PostMapping
    public ResponseEntity<GenericPoolResponseDTO> createGenericPool(
            @Valid @RequestBody GenericPoolRequestDTO request
            ){
        GenericPoolResponseDTO response = genericPoolService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GenericPoolResponseDTO>> listGenericPools(){
        List<GenericPoolResponseDTO> genericPools = genericPoolService.listAllGenericPools();
        return ResponseEntity.ok(genericPools);
    }
}
