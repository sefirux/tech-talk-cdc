package com.jpaz.portfolio.exception;

import java.util.UUID;

public class OperationNotFoundException extends NotFoundException {

    public OperationNotFoundException(UUID id) {
        super("Operation not found: " + id);
    }
}
