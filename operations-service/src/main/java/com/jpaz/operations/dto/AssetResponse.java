package com.jpaz.operations.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Serdeable
public record AssetResponse(
        UUID id,
        String name,
        OffsetDateTime createdAt
) {}
