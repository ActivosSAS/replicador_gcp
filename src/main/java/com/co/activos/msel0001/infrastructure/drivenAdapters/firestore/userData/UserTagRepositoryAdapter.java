package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.userData;

import com.co.activos.msel0001.domain.model.reclutador.tag.gateway.TagGateway;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class UserTagRepositoryAdapter implements TagGateway {

    private static final String COLLECTION = "Users";
    private static final String FIELD_TAGS = "tags";

    private final Firestore firestore;

    public UserTagRepositoryAdapter(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getUserTags(String userId) {
        DocumentSnapshot snapshot = await(documentReference(userId).get(),
                "Error leyendo tags del usuario " + userId);
        return (List<String>) snapshot.get(FIELD_TAGS);
    }

    @Override
    public void updateUserTags(String userId, List<String> tags) {
        await(documentReference(userId).update(FIELD_TAGS, tags),
                "Error actualizando tags del usuario " + userId);
    }

    private DocumentReference documentReference(String userId) {
        return firestore.collection(COLLECTION).document(userId);
    }

    private <T> T await(ApiFuture<T> future, String errorMessage) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(errorMessage, e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(errorMessage, e);
        }
    }
}
