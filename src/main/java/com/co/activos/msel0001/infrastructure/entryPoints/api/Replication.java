package com.co.activos.msel0001.infrastructure.entryPoints.api;

import com.co.activos.msel0001.domain.usecase.StrategyReplicationUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/replication")
@AllArgsConstructor
public class Replication {

    private final StrategyReplicationUseCase replicationUseCase;

    @GetMapping
    public String status() {
        return "Up and running";
    }

    @PostMapping
    public ResponseEntity<String> replicate(@RequestBody List<String> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No event IDs provided");
        }
        eventIds.stream().forEach(replicationUseCase::replicate);
        return ResponseEntity.ok("Events replicated successfully");
    }

}
