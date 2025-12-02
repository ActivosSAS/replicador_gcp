package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.basicInformationData.Converter;

import com.co.activos.msel0001.domain.model.reclutador.basicInformation.User;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.basicInformationData.BasicData;

public class BasicInformationConverter {

    public static User convertToDomain(BasicData basicInformationData) {
        return User.builder()
                .id(basicInformationData.getId())
                .userId(basicInformationData.getUserId())
                .documentType(basicInformationData.getDocumentType())
                .documentNumber(basicInformationData.getNoDocument())
                .name(basicInformationData.getName())
                .lastName(basicInformationData.getLastName())
                .build();
    }


}
