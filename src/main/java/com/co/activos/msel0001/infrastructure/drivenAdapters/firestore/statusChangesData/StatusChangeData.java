package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.statusChangesData;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Document(collectionName = "StatusChangesUserOffer")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeData implements Serializable {
    @DocumentId
    private  String id;
    private String agreementId;
    private String bookId;
    private String deliveryDate;
    private String requestDate;
    private String requisitionNumber;
    private String stateSource;
    private String status;
    private String userId;
}
