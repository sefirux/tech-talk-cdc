package com.jpaz.portfolio.exception;

import java.util.UUID;

public class DuplicatePositionException extends RuntimeException {

    public DuplicatePositionException(UUID accountId, UUID assetId) {
        super("Position already exists for account " + accountId + " and asset " + assetId);
    }
}
