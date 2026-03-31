package com.jpaz.operations.service.mappers;

import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.model.Operation;

public class OperationResponseMapper {

    private OperationResponseMapper() {
        // empty constructor
    }

    public static OperationResponse map(Operation op) {
        return new OperationResponse(
                op.getId(),
                op.getAccountId(),
                op.getAssetId(),
                op.getSide(),
                op.getQuantity(),
                op.getPrice(),
                op.getStatus(),
                op.getCreatedAt(),
                op.getUpdatedAt()
        );
    }

}
