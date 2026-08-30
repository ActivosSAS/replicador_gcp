package com.co.activos.msel0001.domain.model.reclutador.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Tag {
    private final String tagId;
    private final String action;
}
