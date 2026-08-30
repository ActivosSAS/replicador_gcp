package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.candidateTagData.Converter;

import com.co.activos.msel0001.domain.model.reclutador.candidateTag.CandidateTag;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.candidateTagData.CandidateTagData;

public class CandidateTagDataConverter {

    public static CandidateTag buildToDomain(CandidateTagData data) {
        return CandidateTag
                .builder()
                .id(data.getId())
                .name(data.getName())
                .active(data.getActive())
                .build();
    }
}
