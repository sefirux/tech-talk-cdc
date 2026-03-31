package com.jpaz.operations.controller;

import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.service.OperationLookupService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

import java.time.OffsetDateTime;
import java.util.UUID;

@Controller(produces = "application/json")
public class OperationLookupController {

    private final OperationLookupService lookupService;

    public OperationLookupController(OperationLookupService lookupService) {
        this.lookupService = lookupService;
    }

    @Get("/operations")
    public Page<OperationResponse> list(Pageable pageable, @Nullable @QueryValue OffsetDateTime since) {
        if (since != null) {
            return lookupService.findSince(since, pageable);
        }
        return lookupService.findAll(pageable);
    }

    @Get("/operations/account/{accountId}")
    public Page<OperationResponse> listByAccount(UUID accountId, Pageable pageable) {
        return lookupService.findByAccountId(accountId, pageable);
    }

    @Get("/operations/{id}")
    public OperationResponse get(UUID id) {
        return lookupService.findById(id);
    }
}
