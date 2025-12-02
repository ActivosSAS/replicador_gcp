package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.remark.Remark;
import com.co.activos.msel0001.domain.model.reclutador.remark.gateway.RemarkGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RemarkReplicateUseCase implements StrategyReplication {

    private final Gson gson;
    private final RemarkGateway remarkGateway;


    public RemarkReplicateUseCase(Gson gson, RemarkGateway remarkGateway) {
        this.gson = gson;
        this.remarkGateway = remarkGateway;
    }

    @Override
    public void replicate(User information) {
             Optional.ofNullable(remarkGateway.findByUserId(information.getId()))
                .map(remark -> updateRemark(remark, getRemark(information)))
                .filter(remarkUpdate -> !remarkUpdate.equals(remarkGateway.findByUserId(information.getId())))
                .ifPresentOrElse(
                        remarkGateway::save,
                        () -> remarkGateway.save(getRemark(information))
                );
    }

    private Remark getRemark(User information) {
       return buildRemark(information)
                .toBuilder()
                .userId(information.getId())
                .build();
    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.REMARK;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }

    private Remark buildRemark(User information) {
        try {
            return gson.fromJson(information.getInformationToReplicate(), Remark.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format for Remark: " + information.getInformationToReplicate(), e);
        }
    }

    private Remark updateRemark(Remark existingRemark, Remark newRemark) {
        return existingRemark.toBuilder()
                .id(existingRemark.getId())
                .userId(newRemark.getUserId())
                .score(newRemark.getScore())
                .mark(newRemark.getMark())
                .build();
    }
}
