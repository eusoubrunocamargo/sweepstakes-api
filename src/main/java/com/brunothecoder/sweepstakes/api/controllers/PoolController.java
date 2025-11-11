package com.brunothecoder.sweepstakes.api.controllers;

import com.brunothecoder.sweepstakes.api.dto.pool.PoolRequestDTO;
import com.brunothecoder.sweepstakes.api.dto.pool.PoolResponseDTO;
import com.brunothecoder.sweepstakes.application.services.PoolService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pools")
public class PoolController {

    private final PoolService poolService;

    public PoolController(PoolService poolService){
        this.poolService = poolService;
    }

    @PostMapping
    public ResponseEntity<PoolResponseDTO>
    createPool(@RequestBody @Valid PoolRequestDTO dto){
        PoolResponseDTO response = poolService.createPool(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PoolResponseDTO>>
    listPools(){
        return ResponseEntity.ok(poolService.listAllPools());
    }

    @GetMapping("/{poolId}/total-amount")
    public ResponseEntity<BigDecimal> getCachedTotalAmount(@PathVariable UUID poolId){
        BigDecimal total = poolService.getCachedTotalAmount(poolId);
        return ResponseEntity.ok(total);
    }
}
