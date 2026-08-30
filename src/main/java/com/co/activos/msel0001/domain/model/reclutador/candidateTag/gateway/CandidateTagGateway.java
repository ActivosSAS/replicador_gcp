package com.co.activos.msel0001.domain.model.reclutador.candidateTag.gateway;

import com.co.activos.msel0001.domain.model.reclutador.candidateTag.CandidateTag;

public interface CandidateTagGateway {
    CandidateTag findById(String tagId);
}
