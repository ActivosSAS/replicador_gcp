package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.tagAssignmentData;

import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.TagAssignment;
import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.gateway.TagAssignmentGateway;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Escritura directa con el cliente Firestore (no Spring Data) porque
 * expiresAt debe quedar como Timestamp nativo: es el campo sobre el que se
 * activa la politica TTL de Firestore que borra el documento 12 meses
 * despues de la fecha de retiro sin necesidad de barrido/cron. El borrado
 * automatico dispara a su vez la Cloud Function que sincroniza Users.tags.
 */
@Repository
public class TagAssignmentRepositoryAdapter implements TagAssignmentGateway {

    private static final String COLLECTION = "TagAssignments";
    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_TAG_ID = "tagId";
    private static final String FIELD_EXPIRES_AT = "expiresAt";

    private final Firestore firestore;

    public TagAssignmentRepositoryAdapter(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void save(TagAssignment tagAssignment) {
        Map<String, Object> data = new HashMap<>();
        data.put(FIELD_USER_ID, tagAssignment.getUserId());
        data.put(FIELD_TAG_ID, tagAssignment.getTagId());
        data.put(FIELD_EXPIRES_AT, Timestamp.of(Date.from(tagAssignment.getExpiresAt())));

        await(documentReference(tagAssignment.getUserId(), tagAssignment.getTagId()).set(data),
                "Error guardando TagAssignment de " + tagAssignment.getUserId());
    }

    @Override
    public void delete(String userId, String tagId) {
        await(documentReference(userId, tagId).delete(), "Error eliminando TagAssignment de " + userId);
    }

    private DocumentReference documentReference(String userId, String tagId) {
        return firestore.collection(COLLECTION).document(userId + "_" + tagId);
    }

    private void await(ApiFuture<?> future, String errorMessage) {
        try {
            future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(errorMessage, e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(errorMessage, e);
        }
    }
}
