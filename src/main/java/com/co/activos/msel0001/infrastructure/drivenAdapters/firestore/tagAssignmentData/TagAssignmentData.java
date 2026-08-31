package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.tagAssignmentData;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Document(collectionName = "TagAssignments")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TagAssignmentData implements Serializable {

    @DocumentId
    private String id;
    private String userId;
    private String tagId;
    // Campo sobre el que se activa la politica TTL de Firestore.
    private Timestamp expiresAt;
}
