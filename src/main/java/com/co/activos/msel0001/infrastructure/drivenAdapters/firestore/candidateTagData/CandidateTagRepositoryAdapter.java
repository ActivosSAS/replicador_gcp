package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.candidateTagData;

import com.co.activos.msel0001.domain.model.reclutador.candidateTag.CandidateTag;
import com.co.activos.msel0001.domain.model.reclutador.candidateTag.gateway.CandidateTagGateway;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.candidateTagData.Converter.CandidateTagDataConverter;
import org.springframework.stereotype.Repository;

@Repository
public class CandidateTagRepositoryAdapter implements CandidateTagGateway {

    private final CandidateTagDataRepository candidateTagDataRepository;

    public CandidateTagRepositoryAdapter(CandidateTagDataRepository candidateTagDataRepository) {
        this.candidateTagDataRepository = candidateTagDataRepository;
    }

    @Override
    public CandidateTag findById(String tagId) {
        return candidateTagDataRepository.findById(tagId)
                .map(CandidateTagDataConverter::buildToDomain)
                .block();
    }
}
