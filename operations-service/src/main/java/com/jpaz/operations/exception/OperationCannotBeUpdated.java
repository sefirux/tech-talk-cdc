package com.jpaz.operations.exception;

import com.jpaz.operations.model.OperationStatus;

import java.util.UUID;

public class OperationCannotBeUpdated extends BadRequestException {

    public OperationCannotBeUpdated(UUID id, OperationStatus currentStatus, OperationStatus newStatus) {
        super("Cannot update operation %s from state %s to %s".formatted(id, currentStatus, newStatus));
    }

}
