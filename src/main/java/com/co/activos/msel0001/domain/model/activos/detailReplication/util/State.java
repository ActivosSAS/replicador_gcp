package com.co.activos.msel0001.domain.model.activos.detailReplication.util;

public enum State {

    PENDING("PENDING"),
    PROCESSED("PROCESSED"),
    NOT_FOUND("NOT_FOUND"),
    ERROR("ERROR");

    private final String value;

    State(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
