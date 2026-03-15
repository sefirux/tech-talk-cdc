package com.jpaz.portfolio.exception;

import java.util.UUID;

public class PositionNotFoundException extends NotFoundException {

    public PositionNotFoundException(UUID id) {
        super("Position not found: " + id);
    }
}
