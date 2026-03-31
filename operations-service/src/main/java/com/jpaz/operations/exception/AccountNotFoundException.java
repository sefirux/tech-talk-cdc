package com.jpaz.operations.exception;

import java.util.UUID;

public class AccountNotFoundException extends NotFoundException {

    public AccountNotFoundException(UUID id) {
        super("Account not found: " + id);
    }
}
