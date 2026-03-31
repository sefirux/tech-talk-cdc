package com.jpaz.operations.exception;

import java.util.UUID;

public class OperationNotFoundException extends NotFoundException {

    public OperationNotFoundException(UUID id) {
        super("Operation not found: " + id);
    }
}
