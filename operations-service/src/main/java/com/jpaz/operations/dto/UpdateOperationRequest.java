package com.jpaz.operations.dto;

import com.jpaz.operations.model.OperationStatus;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotNull;

@Serdeable
public record UpdateOperationRequest(

        @NotNull
        OperationStatus status

) {}
