package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.reclutador.userDocumentary.UserDocumentaryReview;
import com.co.activos.msel0001.domain.model.reclutador.userDocumentary.gateway.UserDocumentaryGateway;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDocumentaryReplicateUseCase implements StrategyReplication {

    private final Gson gson;
    private final UserDocumentaryGateway userDocumentaryGateway;

    @Override
    public void replicate(User information) {
        UserDocumentaryReview userDocumentaryReviewToSave = buildUserDocumentaryReview(information)
                .toBuilder()
                .userId(information.getId())
                .build();

        UserDocumentaryReview existingUserDocumentaryReview = userDocumentaryGateway.findByUserId(information.getId());

        if(existingUserDocumentaryReview != null) {
            UserDocumentaryReview updated = updateUserDocumentaryReview(existingUserDocumentaryReview, userDocumentaryReviewToSave);
            if (existingUserDocumentaryReview.equals(updated)) {
                return;
            }
            userDocumentaryReviewToSave = updated;
        }

        userDocumentaryGateway.saveUserDocumentary(userDocumentaryReviewToSave);
    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.USER_DOCUMENTARY;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }


    private UserDocumentaryReview buildUserDocumentaryReview(User information) {
        try {
            return gson.fromJson(information.getInformationToReplicate(), UserDocumentaryReview.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format for UserDocumentaryReview: " + information.getInformationToReplicate(), e);
        }
    }

    private UserDocumentaryReview updateUserDocumentaryReview(UserDocumentaryReview existingUserDocumentaryReview, UserDocumentaryReview newUserDocumentaryReview) {

        return existingUserDocumentaryReview.toBuilder()
                .id(existingUserDocumentaryReview.getId())
                .userId(newUserDocumentaryReview.getUserId())
                .requisitionNumber(newUserDocumentaryReview.getRequisitionNumber())
                .updateDate(newUserDocumentaryReview.getUpdateDate())
                .build();
    }
}
