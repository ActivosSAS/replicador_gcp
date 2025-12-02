package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.exceptions.ReplicationException;
import com.co.activos.msel0001.domain.model.reclutador.requisition.RequisitionStatus;
import com.co.activos.msel0001.domain.model.reclutador.requisition.gateway.RequisicionStatusGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RequisitionStatusReplicateUseCase implements StrategyReplication {

    private final RequisicionStatusGateway requisicionStatusGateway;
    private final Gson gson;

    public RequisitionStatusReplicateUseCase(RequisicionStatusGateway requisicionStatusGateway, Gson gson) {
        this.requisicionStatusGateway = requisicionStatusGateway;
        this.gson = gson;
    }

    @Override
    public void replicate(User information) {
        RequisitionStatus requisitionStatus = buildRequisitionStatus(information);
        RequisitionStatus existingRequisitionStatus = requisicionStatusGateway
                .findByRequisitionNumber(requisitionStatus.getRequisitionNumber());
        if (existingRequisitionStatus != null) {

            if (existingRequisitionStatus.getStatus().equals(requisitionStatus.getStatus())) {
                return;
            }

            requisitionStatus = updateRequisitionStatus(requisitionStatus, existingRequisitionStatus);

        }
        requisicionStatusGateway.save(requisitionStatus);
    }


    private RequisitionStatus updateRequisitionStatus(RequisitionStatus existingRequisition, RequisitionStatus requisitionToSave) {
        return existingRequisition.toBuilder()
                .id(requisitionToSave.getId())
                .requisitionNumber(existingRequisition.getRequisitionNumber())
                .status(existingRequisition.getStatus())
                .build();
    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.REQUISITION_STATUS;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return false;
    }

    private RequisitionStatus buildRequisitionStatus(User information) {
        try {
            return gson.fromJson(information.getInformationToReplicate(), RequisitionStatus.class);
        } catch (Exception e) {
            throw new ReplicationException("Error building RequisitionStatus from JSON: " + e.getMessage(), e);
        }
    }
}
