package com.jpaz.operations.model;

public enum OperationStatus {
    PENDING(false),
    EXECUTED(true),
    CANCELLED(true),
    FAILED(true);

    private final boolean isFinalStatus;

    OperationStatus(boolean isFinalStatus) {
        this.isFinalStatus = isFinalStatus;
    }

    public boolean isFinalStatus() {
        return isFinalStatus;
    }
}
