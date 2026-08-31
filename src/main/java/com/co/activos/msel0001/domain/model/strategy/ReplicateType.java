package com.co.activos.msel0001.domain.model.strategy;

import lombok.Getter;

@Getter
public enum ReplicateType {

    AGGREGATE("3"),
    BLOCK("1"),
    UNLOCK("10"),
    REMARK("5"),
    REQUISITION("2"),
    REQUISITION_STATUS("7"),
    USER_DOCUMENTARY("4"),
    BASIC_INFORMATION("6"),
    STATUS_CHANGE("8"),
    // idConfig 14 en RHU.Replication_Config (test), status Inactive hasta terminar
    // el flujo completo. 9, 11, 12 y 13 ya estaban tomados por otros eventos.
    TAG("14");

    private final String value;

    ReplicateType(String value) {
        this.value = value;
    }

}
