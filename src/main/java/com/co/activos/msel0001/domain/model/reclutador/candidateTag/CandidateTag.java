package com.co.activos.msel0001.domain.model.reclutador.candidateTag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CandidateTag {
    private final String id;
    private final String name;
    private final Boolean active;
}
