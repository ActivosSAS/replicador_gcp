package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.tagAssignmentData.Converter;

import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.TagAssignment;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.tagAssignmentData.TagAssignmentData;
import com.google.cloud.Timestamp;

import java.util.Date;

public class TagAssignmentDataConverter {

    public static TagAssignmentData buildToData(TagAssignment tagAssignment) {
        return TagAssignmentData.builder()
                .id(documentId(tagAssignment.getUserId(), tagAssignment.getTagId()))
                .userId(tagAssignment.getUserId())
                .tagId(tagAssignment.getTagId())
                .expiresAt(Timestamp.of(Date.from(tagAssignment.getExpiresAt())))
                .build();
    }

    public static String documentId(String userId, String tagId) {
        return userId + "_" + tagId;
    }
}
