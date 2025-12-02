package com.co.activos.msel0001.domain.model.reclutador.agreement.gateway;

import com.co.activos.msel0001.domain.model.reclutador.agreement.Agreement;

public interface AgreementGateway {
    Agreement findByUserId(String userId);
    Agreement findByUserIdAndAgreementId(String userId, String agreementId);
    void saveAgreement(Agreement agreement);
}
