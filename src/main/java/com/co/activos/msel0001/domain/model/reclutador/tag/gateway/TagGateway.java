package com.co.activos.msel0001.domain.model.reclutador.tag.gateway;

import java.util.List;

public interface TagGateway {
    List<String> getUserTags(String userId);
    void updateUserTags(String userId, List<String> tags);
}
