package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.candidateTagData;

import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Document(collectionName = "CandidateTags")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CandidateTagData implements Serializable {
    private String id;
    private String name;
    private String color;
    private String icon;
    private Boolean active;
}
