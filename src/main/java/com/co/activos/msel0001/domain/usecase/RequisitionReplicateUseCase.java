package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.requisition.Requisition;
import com.co.activos.msel0001.domain.model.reclutador.requisition.gateway.RequisitionGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Service;

@Service
public class RequisitionReplicateUseCase implements StrategyReplication {
    private final Gson gson;
    private final RequisitionGateway requisitionGateway;

    public RequisitionReplicateUseCase(Gson gson, RequisitionGateway requisitionGateway) {
        this.gson = gson;
        this.requisitionGateway = requisitionGateway;
    }

    @Override
    public void replicate(User information) {
        Requisition requisitionToSave = buildRequisition(information)
                .toBuilder()
                .userId(information.getId())
                .build();

        Requisition existingRequisition = requisitionGateway.findByIdAndRequisitionNumber(information.getId(),
                requisitionToSave.getRequisitionNumber());

        if (existingRequisition != null) {

            requisitionToSave = updaRequisition(existingRequisition, requisitionToSave);
            if (existingRequisition.equals(requisitionToSave)) {
                return;
            }
        }
        requisitionGateway.save(requisitionToSave);
    }


    private Requisition buildRequisition(User information) {
        try {
            return gson.fromJson(information.getInformationToReplicate(), Requisition.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format for Block: " + information.getInformationToReplicate(), e);
        }
    }

    private Requisition updaRequisition(Requisition existingRemark, Requisition newRemark) {

        return existingRemark.toBuilder()
                .id(existingRemark.getId())
                .userId(newRemark.getUserId())
                .requisitionNumber(newRemark.getRequisitionNumber())
                .deliveryDate(newRemark.getDeliveryDate())
                .requestDate(newRemark.getRequestDate())
                .selectionStatus(newRemark.getSelectionStatus())
                .status(newRemark.getStatus())
                .build();
    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.REQUISITION;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }
}
