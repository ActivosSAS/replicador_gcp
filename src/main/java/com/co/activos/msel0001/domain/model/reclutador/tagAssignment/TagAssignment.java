package com.co.activos.msel0001.domain.model.reclutador.tagAssignment;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
public class TagAssignment {
    private final String userId;
    private final String tagId;
    private final Instant expiresAt;
}
