package com.co.activos.msel0001.infrastructure.entryPoints.api;

import com.co.activos.msel0001.domain.usecase.StrategyReplicationUseCase;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/replication")
@AllArgsConstructor
public class Replication {

    private final StrategyReplicationUseCase replicationUseCase;

    @GetMapping
    public String status() {
        return "Up and running";
    }

    @GetMapping("/{idEvent}")
    public String replicate(@PathVariable("idEvent") String idEvent) {
        replicationUseCase.replicate(idEvent);
        return "Event replicated";
    }

}
