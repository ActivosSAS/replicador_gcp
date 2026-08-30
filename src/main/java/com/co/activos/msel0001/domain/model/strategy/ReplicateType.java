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
    // TODO: "9" es un placeholder. Confirmar el idConfig real que Oracle va a enviar para este evento.
    TAG("9");

    private final String value;

    ReplicateType(String value) {
        this.value = value;
    }

}
