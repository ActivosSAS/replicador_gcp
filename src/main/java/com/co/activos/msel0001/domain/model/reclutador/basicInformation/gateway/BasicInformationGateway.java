package com.co.activos.msel0001.domain.model.reclutador.basicInformation.gateway;

import com.co.activos.msel0001.domain.model.reclutador.basicInformation.User;

public interface BasicInformationGateway {
    User getBasicInformation(String typeDocument, String documentNumber);
}
