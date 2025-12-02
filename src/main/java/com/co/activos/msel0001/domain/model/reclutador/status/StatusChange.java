package com.co.activos.msel0001.domain.model.reclutador.status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class StatusChange {
    private final String id;
    private final String agreementId;
    private final String bookId;
    private final String deliveryDate;
    private final String requestDate;
    private final String requisitionNumber;
    private final String stateSource;
    private final String status;
    private final String userId;
}
