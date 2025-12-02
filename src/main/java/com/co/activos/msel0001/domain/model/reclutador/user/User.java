package com.co.activos.msel0001.domain.model.reclutador.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class User {
    private final String id;
    private final String documentType;
    private final String email;
    private final String userType;
    private final String role;
    private final String idNumber;
    private final String informationToReplicate;
}
