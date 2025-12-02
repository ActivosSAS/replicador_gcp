package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.statusChangesData.converter;

import com.co.activos.msel0001.domain.model.reclutador.status.StatusChange;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.statusChangesData.StatusChangeData;

public class StatusChangeConverter {

    public static StatusChange toStatusChange(StatusChangeData statusChangeData) {
        return StatusChange.builder()
                .id(statusChangeData.getId())
                .deliveryDate(statusChangeData.getDeliveryDate())
                .requestDate(statusChangeData.getRequestDate())
                .bookId(statusChangeData.getBookId())
                .agreementId(statusChangeData.getAgreementId())
                .stateSource(statusChangeData.getStateSource())
                .userId(statusChangeData.getUserId())
                .requisitionNumber(statusChangeData.getRequisitionNumber())
                .status(statusChangeData.getStatus())
                .build();
    }

    public static StatusChangeData toStatusChangeData(StatusChange statusChange) {
        return StatusChangeData.builder()
                .id(statusChange.getId())
                .deliveryDate(statusChange.getDeliveryDate())
                .requestDate(statusChange.getRequestDate())
                .bookId(statusChange.getBookId())
                .agreementId(statusChange.getAgreementId())
                .stateSource(statusChange.getStateSource())
                .userId(statusChange.getUserId())
                .requisitionNumber(statusChange.getRequisitionNumber())
                .status(statusChange.getStatus())
                .build();
    }

}
