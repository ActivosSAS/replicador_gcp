package com.co.activos.msel0001.domain.usecase;


import com.co.activos.msel0001.domain.model.reclutador.basicInformation.gateway.BasicInformationGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicInformationReplicateUseCase implements StrategyReplication {

    private final BasicInformationGateway basicInformationGateway;

    @Override
    public void replicate(User information) {


    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.BASIC_INFORMATION;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }
}
