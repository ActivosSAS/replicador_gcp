package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.statusChangesData;

import com.co.activos.msel0001.domain.model.reclutador.status.StatusChange;
import com.co.activos.msel0001.domain.model.reclutador.status.gateway.StatusChangeGateway;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.statusChangesData.converter.StatusChangeConverter;
import org.springframework.stereotype.Repository;

@Repository
public class StatusChangeRepositoryAdapter implements StatusChangeGateway {

    private final StatusChangeDataRepository statusChangeDataRepository;

    public StatusChangeRepositoryAdapter(StatusChangeDataRepository statusChangeDataRepository) {
        this.statusChangeDataRepository = statusChangeDataRepository;
    }

    @Override
    public StatusChange findByIDAndRequisitionNumber(String id, String requisitionNumber) {
        return statusChangeDataRepository.findByUserIdAndRequisitionNumber(id, requisitionNumber)
                .map(StatusChangeConverter::toStatusChange)
                .block();
    }

    @Override
    public StatusChange save(StatusChange statusChange) {
        return statusChangeDataRepository
                .save(StatusChangeConverter.toStatusChangeData(statusChange))
                .map(StatusChangeConverter::toStatusChange)
                .block();
    }
}
