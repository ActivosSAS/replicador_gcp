package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.userData;

import com.co.activos.msel0001.domain.model.reclutador.tag.gateway.TagGateway;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A diferencia de UserRepositoryAdapter (lectura via Spring Data), este
 * gateway escribe solo el campo "tags" del documento de Users con un
 * update() parcial real, para no arriesgar sobreescribir el resto de
 * campos del documento (Spring Data Firestore save() no garantiza merge
 * y UserData no mapea todos los campos reales de Users).
 */
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
        try {
            DocumentSnapshot snapshot = documentReference(userId).get().get();
            Object tags = snapshot.get(FIELD_TAGS);
            return (List<String>) tags;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Error leyendo tags del usuario " + userId, e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Error leyendo tags del usuario " + userId, e);
        }
    }

    @Override
    public void updateUserTags(String userId, List<String> tags) {
        try {
            documentReference(userId).update(FIELD_TAGS, tags).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Error actualizando tags del usuario " + userId, e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Error actualizando tags del usuario " + userId, e);
        }
    }

    private DocumentReference documentReference(String userId) {
        return firestore.collection(COLLECTION).document(userId);
    }
}
