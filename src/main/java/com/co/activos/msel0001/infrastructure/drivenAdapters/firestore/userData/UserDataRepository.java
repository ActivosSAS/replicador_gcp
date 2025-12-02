package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.userData;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import reactor.core.publisher.Mono;

public interface UserDataRepository extends FirestoreReactiveRepository<UserData> {
    Mono<UserData> findByDocumentTypeAndIdNumber(String documentType, String idNumber);
}
