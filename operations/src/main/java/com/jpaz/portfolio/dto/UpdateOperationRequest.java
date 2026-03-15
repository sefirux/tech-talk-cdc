package com.jpaz.portfolio.dto;

import com.jpaz.portfolio.model.OperationStatus;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record UpdateOperationRequest(

        @NotNull
        OperationStatus status

) {}
