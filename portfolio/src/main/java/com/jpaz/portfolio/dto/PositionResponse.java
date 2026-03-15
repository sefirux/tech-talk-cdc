package com.jpaz.portfolio.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Serdeable
public record PositionResponse(
        UUID id,
        UUID accountId,
        UUID assetId,
        BigDecimal quantity,
        BigDecimal averagePrice,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
