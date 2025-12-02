package com.co.activos.msel0001.domain.model.reclutador.user.gateway;

import com.co.activos.msel0001.domain.model.reclutador.user.User;

public interface UserGateway {
    User getUser(String typeDocument, String documentNumber);
}
