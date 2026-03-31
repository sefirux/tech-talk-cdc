package com.jpaz.operations.controller;

import com.jpaz.operations.dto.CreateOperationRequest;
import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.service.OperationCreateService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import jakarta.validation.Valid;

@Controller("/operations")
public class OperationCreateController {

    private final OperationCreateService operationCreateService;

    public OperationCreateController(OperationCreateService operationCreateService) {
        this.operationCreateService = operationCreateService;
    }

    // dual-write
    @Post("/v1")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV1(@Body @Valid CreateOperationRequest request) {
        return operationCreateService.create(request);
    }

    // outbox
    @Post("/v2")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV2(@Body @Valid CreateOperationRequest request) {
        return operationCreateService.create(request);
    }

}
