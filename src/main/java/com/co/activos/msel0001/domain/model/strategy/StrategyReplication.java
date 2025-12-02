package com.co.activos.msel0001.domain.model.strategy;

import com.co.activos.msel0001.domain.model.reclutador.user.User;

public interface StrategyReplication {

    void replicate(User information);

    ReplicateType getReplicateType();

     boolean requiresDocumentValidation();
}
