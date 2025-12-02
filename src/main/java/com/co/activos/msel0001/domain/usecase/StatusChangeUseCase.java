package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.status.StatusChange;
import com.co.activos.msel0001.domain.model.reclutador.status.gateway.StatusChangeGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Service;

@Service
public class StatusChangeUseCase implements StrategyReplication {

    private final Gson gson;
    private final StatusChangeGateway statusChangeGateway;

    public StatusChangeUseCase(Gson gson, StatusChangeGateway statusChangeGateway) {
        this.gson = gson;
        this.statusChangeGateway = statusChangeGateway;
    }

    @Override
    public void replicate(User information) {

        StatusChange statusChangeToSave = buildStatusChange(information)
                .toBuilder()
                .userId(information.getId())
                .build();

        StatusChange existingStatusChange = statusChangeGateway.findByIDAndRequisitionNumber(information.getId(), statusChangeToSave.getRequisitionNumber());

        if (existingStatusChange != null) {
            statusChangeToSave = updateStatusChange(existingStatusChange, statusChangeToSave);
            if (existingStatusChange.equals(statusChangeToSave)) {
                return;
            }
        }
        statusChangeGateway.save(statusChangeToSave);

    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.STATUS_CHANGE;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }

    private StatusChange buildStatusChange(User information) {
        try {
            return gson.fromJson(information.getInformationToReplicate(), StatusChange.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format for Block: " + information.getInformationToReplicate(), e);
        }
    }

    private StatusChange updateStatusChange(StatusChange existingStatusChange, StatusChange statusChangeToSave) {
        return existingStatusChange.toBuilder()
                .id(existingStatusChange.getId())
                .deliveryDate(statusChangeToSave.getDeliveryDate())
                .requestDate(statusChangeToSave.getRequestDate())
                .bookId(statusChangeToSave.getBookId())
                .agreementId(statusChangeToSave.getAgreementId())
                .userId(existingStatusChange.getUserId())
                .requisitionNumber(statusChangeToSave.getRequisitionNumber())
                .status(statusChangeToSave.getStatus())
                .build();
    }
}
