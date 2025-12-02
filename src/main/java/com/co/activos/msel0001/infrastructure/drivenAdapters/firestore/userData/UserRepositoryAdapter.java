package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.userData;

import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.reclutador.user.gateway.UserGateway;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.userData.Converter.UserConverter;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserGateway {

    private final UserDataRepository userDataRepository;

    public UserRepositoryAdapter(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public User getUser(String typeDocument, String documentNumber) {
        return userDataRepository.findByDocumentTypeAndIdNumber(typeDocument, documentNumber)
                .map(UserConverter::toUser)
                .block();
    }
}

