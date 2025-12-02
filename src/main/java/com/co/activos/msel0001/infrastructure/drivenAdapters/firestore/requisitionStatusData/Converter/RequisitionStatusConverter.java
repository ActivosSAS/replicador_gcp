package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.requisitionStatusData.Converter;

import com.co.activos.msel0001.domain.model.reclutador.requisition.RequisitionStatus;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.requisitionStatusData.RequisitionStatusData;
import org.springframework.stereotype.Component;

@Component
public class RequisitionStatusConverter {
    
    public RequisitionStatus toEntity(RequisitionStatusData data) {
        if (data == null) {
            return null;
        }
        return RequisitionStatus.builder()
                .id(data.getId())
                .requisitionNumber(data.getRequisitionNumber())
                .status(data.getStatus())
                .build();
    }
    
    public RequisitionStatusData toData(RequisitionStatus entity) {
        if (entity == null) {
            return null;
        }
        return RequisitionStatusData.builder()
                .id(entity.getId())
                .requisitionNumber(entity.getRequisitionNumber())
                .status(entity.getStatus())
                .build();
    }
}
