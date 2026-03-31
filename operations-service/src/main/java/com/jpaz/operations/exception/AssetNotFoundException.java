package com.jpaz.operations.exception;

import java.util.UUID;

public class AssetNotFoundException extends NotFoundException {

    public AssetNotFoundException(UUID id) {
        super("Asset not found: " + id);
    }
}
