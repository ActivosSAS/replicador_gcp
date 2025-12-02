package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.userData.Converter;

import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.userData.UserData;

public class UserConverter {

    public static User toUser(UserData userData) {
        return User.builder()
                .id(userData.getId())
                .documentType(userData.getDocumentType())
                .email(userData.getEmail())
                .userType(userData.getUserType())
                .role(userData.getRole())
                .idNumber(userData.getIdNumber())
                .build();
    }

}
