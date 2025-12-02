package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.requisitionStatusData;

import com.co.activos.msel0001.domain.model.reclutador.requisition.RequisitionStatus;
import com.co.activos.msel0001.domain.model.reclutador.requisition.gateway.RequisicionStatusGateway;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.requisitionStatusData.Converter.RequisitionStatusConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RequisitionStatusRepositoryAdapter implements RequisicionStatusGateway {

    private final RequisitionStatusDataRepository repository;
    private final RequisitionStatusConverter converter;

    @Override
    public RequisitionStatus save(RequisitionStatus requisitionStatus) {
        return repository.save(converter.toData(requisitionStatus))
                .map(converter::toEntity).block();
    }

    @Override
    public RequisitionStatus findByRequisitionNumber(String id) {
        return repository.findByRequisitionNumber(id)
                .map(converter::toEntity)
                .block();
    }
}
