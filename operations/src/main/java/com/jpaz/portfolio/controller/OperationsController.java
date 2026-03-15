package com.jpaz.portfolio.controller;

import com.jpaz.portfolio.dto.CreateOperationRequest;
import com.jpaz.portfolio.dto.OperationResponse;
import com.jpaz.portfolio.dto.UpdateOperationRequest;
import com.jpaz.portfolio.service.OperationService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import jakarta.validation.Valid;

import java.util.UUID;

@Controller("/operations")
public class OperationsController {

    private final OperationService operationService;

    public OperationsController(OperationService operationService) {
        this.operationService = operationService;
    }

    @Get
    public Page<OperationResponse> list(Pageable pageable) {
        return operationService.findAll(pageable);
    }

    @Get("/account/{accountId}")
    public Page<OperationResponse> listByAccount(UUID accountId, Pageable pageable) {
        return operationService.findByAccountId(accountId, pageable);
    }

    @Get("/{id}")
    public OperationResponse get(UUID id) {
        return operationService.findById(id);
    }

    @Post
    @Status(HttpStatus.CREATED)
    public OperationResponse create(@Body @Valid CreateOperationRequest request) {
        return operationService.create(request);
    }

    @Put("/{id}")
    public OperationResponse update(UUID id, @Body @Valid UpdateOperationRequest request) {
        return operationService.update(id, request);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(UUID id) {
        operationService.delete(id);
    }
}
