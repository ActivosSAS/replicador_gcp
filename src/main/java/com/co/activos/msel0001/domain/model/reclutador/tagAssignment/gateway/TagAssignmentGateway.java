package com.co.activos.msel0001.domain.model.reclutador.tagAssignment.gateway;

import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.TagAssignment;

public interface TagAssignmentGateway {
    void save(TagAssignment tagAssignment);

    void delete(String userId, String tagId);
}
