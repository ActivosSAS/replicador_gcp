package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.tagAssignmentData;

import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.TagAssignment;
import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.gateway.TagAssignmentGateway;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.tagAssignmentData.Converter.TagAssignmentDataConverter;
import org.springframework.stereotype.Repository;

@Repository
public class TagAssignmentRepositoryAdapter implements TagAssignmentGateway {

    private final TagAssignmentDataRepository tagAssignmentDataRepository;

    public TagAssignmentRepositoryAdapter(TagAssignmentDataRepository tagAssignmentDataRepository) {
        this.tagAssignmentDataRepository = tagAssignmentDataRepository;
    }

    @Override
    public void save(TagAssignment tagAssignment) {
        tagAssignmentDataRepository.save(TagAssignmentDataConverter.buildToData(tagAssignment)).block();
    }

    @Override
    public void delete(String userId, String tagId) {
        tagAssignmentDataRepository.deleteById(TagAssignmentDataConverter.documentId(userId, tagId)).block();
    }
}
