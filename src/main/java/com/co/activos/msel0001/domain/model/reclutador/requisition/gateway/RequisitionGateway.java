package com.co.activos.msel0001.domain.model.reclutador.requisition.gateway;

import com.co.activos.msel0001.domain.model.reclutador.requisition.Requisition;

public interface RequisitionGateway {
    Requisition findByUserId(String id);
    Requisition findByIdAndRequisitionNumber(String id, String requisitionNumber);
    Requisition save(Requisition requisition);
}
