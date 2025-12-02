package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.statusChangesData;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import reactor.core.publisher.Mono;

public interface StatusChangeDataRepository extends FirestoreReactiveRepository<StatusChangeData> {
    Mono<StatusChangeData> findByUserIdAndRequisitionNumber(String id, String requisitionNumber);
}
