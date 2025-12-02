package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.exceptions.ReplicationException;
import com.co.activos.msel0001.domain.model.reclutador.agreement.Agreement;
import com.co.activos.msel0001.domain.model.reclutador.agreement.gateway.AgreementGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgreementReplicateUseCase implements StrategyReplication {


    private final Gson gson;
    private final AgreementGateway agreementGateway;

    @Override
    public void replicate(User information) {

        Agreement agreementToSave = buildAgreement(information)
                .toBuilder()
                .userId(information.getId())
                .build();

        Agreement existingAgreement = agreementGateway.findByUserIdAndAgreementId(information.getId(), agreementToSave.getAgreementId());

        if (existingAgreement != null) {
            agreementToSave = updateAgreement(existingAgreement, agreementToSave);
            if (existingAgreement.equals(agreementToSave)) {
                return;
            }
        }
        agreementGateway.saveAgreement(agreementToSave);
    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.AGGREGATE;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }

    private Agreement buildAgreement(User information) {
        try {
            return gson.fromJson(information.getInformationToReplicate(), Agreement.class);
        } catch (JsonSyntaxException e) {
            throw new ReplicationException("Invalid JSON format for Agreement: " + information.getInformationToReplicate(), e);
        }
    }

    private Agreement updateAgreement(Agreement existingAgreement, Agreement newAgreement) {

        return existingAgreement.toBuilder()
                .id(existingAgreement.getId())
                .userId(newAgreement.getUserId())
                .agreementId(newAgreement.getAgreementId())
                .status(newAgreement.getStatus())
                .build();
    }


}
