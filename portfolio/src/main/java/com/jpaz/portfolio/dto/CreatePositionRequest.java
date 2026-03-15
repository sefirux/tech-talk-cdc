package com.jpaz.portfolio.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Serdeable
public record CreatePositionRequest(

        @NotNull
        UUID accountId,

        @NotNull
        UUID assetId,

        @NotNull
        @DecimalMin(value = "0", message = "quantity must be >= 0")
        BigDecimal quantity,

        @NotNull
        @DecimalMin(value = "0", message = "averagePrice must be >= 0")
        BigDecimal averagePrice

) {}
