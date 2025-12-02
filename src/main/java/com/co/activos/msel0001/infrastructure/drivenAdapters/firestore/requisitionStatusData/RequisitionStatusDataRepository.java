package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.requisitionStatusData;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RequisitionStatusDataRepository extends FirestoreReactiveRepository<RequisitionStatusData> {
  Mono<RequisitionStatusData> findByRequisitionNumber(String requisitionNumber);
}
