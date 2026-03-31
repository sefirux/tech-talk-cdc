package com.jpaz.operations.dto;

import com.jpaz.operations.model.OperationSide;
import com.jpaz.operations.model.OperationStatus;
import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Serdeable
public record OperationResponse(
        UUID id,
        UUID accountId,
        UUID assetId,
        OperationSide side,
        BigDecimal quantity,
        BigDecimal price,
        OperationStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
