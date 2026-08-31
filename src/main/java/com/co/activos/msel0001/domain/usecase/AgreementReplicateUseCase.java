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

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AgreementReplicateUseCase implements StrategyReplication {

    // Un contrato activo implica que la persona ya no deberia tener el tag
    // "Extrabajador" (dato de negocio: no puede tener el tag si tiene
    // contrato activo con la EST o con una empresa usuaria). Se resuelve
    // aqui, reusando el evento de contratos que Oracle ya envia de forma
    // confiable en cada cambio de estado, en vez de pedir un trigger nuevo
    // para el reingreso.
    private static final String EXTRABAJADOR_TAG_ID = "extrabajador";
    private static final String ACTIVE_STATUS_PREFIX = "ACT";

    private final Gson gson;
    private final AgreementGateway agreementGateway;
    private final TagRemovalService tagRemovalService;

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

        if (isActiveStatus(agreementToSave.getStatus())) {
            tagRemovalService.remove(agreementToSave.getUserId(), EXTRABAJADOR_TAG_ID);
        }
    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.AGGREGATE;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }

    private boolean isActiveStatus(String status) {
        return status != null && status.trim().toUpperCase(Locale.ROOT).startsWith(ACTIVE_STATUS_PREFIX);
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
