package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.requisitionStatusData;


import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Document(collectionName = "RequisitionStatus")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionStatusData implements Serializable {
    @DocumentId
    private String id;
    private String requisitionNumber;
    private String status;
}
