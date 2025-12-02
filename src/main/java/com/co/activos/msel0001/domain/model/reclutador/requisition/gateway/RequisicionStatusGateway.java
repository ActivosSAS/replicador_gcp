package com.co.activos.msel0001.domain.model.reclutador.requisition.gateway;


import com.co.activos.msel0001.domain.model.reclutador.requisition.RequisitionStatus;

public interface RequisicionStatusGateway {
    RequisitionStatus save(RequisitionStatus requisitionStatus);
    RequisitionStatus findByRequisitionNumber(String requisitionNumber);
}
