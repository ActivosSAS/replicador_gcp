package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.tag.gateway.TagGateway;
import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.gateway.TagAssignmentGateway;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Remocion de un tag de Users.tags, compartida entre distintos disparadores:
 * el evento explicito de tags (TagReplicateUseCase, action REMOVE) y
 * cualquier otro evento de negocio que deba limpiar un tag como efecto
 * secundario (ej. AgreementReplicateUseCase al ver un contrato activo,
 * caso "Extrabajador" + reingreso). Idempotente: no falla si el usuario no
 * tiene el tag o no habia TagAssignment pendiente.
 */
@Service
public class TagRemovalService {

    private final TagGateway tagGateway;
    private final TagAssignmentGateway tagAssignmentGateway;

    public TagRemovalService(TagGateway tagGateway, TagAssignmentGateway tagAssignmentGateway) {
        this.tagGateway = tagGateway;
        this.tagAssignmentGateway = tagAssignmentGateway;
    }

    public void remove(String userId, String tagId) {
        List<String> currentTags = new ArrayList<>(
                Optional.ofNullable(tagGateway.getUserTags(userId)).orElse(new ArrayList<>()));

        if (currentTags.remove(tagId)) {
            tagGateway.updateUserTags(userId, currentTags);
        }

        tagAssignmentGateway.delete(userId, tagId);
    }
}
