package com.co.activos.msel0001.domain.model.reclutador.requisition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class RequisitionStatus {
    private final String id;
    private final String requisitionNumber;
    private final String status;
}
