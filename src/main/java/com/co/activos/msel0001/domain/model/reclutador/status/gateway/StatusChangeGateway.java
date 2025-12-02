package com.co.activos.msel0001.domain.model.reclutador.status.gateway;

import com.co.activos.msel0001.domain.model.reclutador.status.StatusChange;

public interface StatusChangeGateway {
    StatusChange findByIDAndRequisitionNumber(String id, String requisitionNumber);
    StatusChange save(StatusChange statusChange);
}
