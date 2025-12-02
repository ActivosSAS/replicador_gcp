package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.block.Block;
import com.co.activos.msel0001.domain.model.reclutador.block.gateway.BlockGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;

import org.springframework.stereotype.Service;

@Service
public class UnlockReplicateUseCase implements StrategyReplication {

    private final BlockGateway blockGateway;

    public UnlockReplicateUseCase(BlockGateway blockGateway) {
        this.blockGateway = blockGateway;
    }

    @Override
    public void replicate(User information) {
        Block existingBlock = blockGateway.getBlock(information.getId());
        if (existingBlock != null) {
            blockGateway.deleteBlock(existingBlock);
        }
    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.UNLOCK;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }
}
