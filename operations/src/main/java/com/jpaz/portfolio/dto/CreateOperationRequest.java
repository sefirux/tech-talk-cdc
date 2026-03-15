package com.jpaz.portfolio.dto;

import com.jpaz.portfolio.model.OperationSide;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Serdeable
public record CreateOperationRequest(

        @NotNull
        UUID accountId,

        @NotNull
        UUID assetId,

        @NotNull
        OperationSide side,

        @NotNull
        @DecimalMin(value = "0.000001", message = "quantity must be greater than zero")
        BigDecimal quantity,

        @NotNull
        @DecimalMin(value = "0.000001", message = "price must be greater than zero")
        BigDecimal price

) {}
