package com.jpaz.operations.controller;

import com.jpaz.operations.dto.CreateOperationRequest;
import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.service.OperationCreateService;
import com.jpaz.operations.service.OperationProcessor;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import jakarta.validation.Valid;

@Controller("/operations")
public class OperationCreateController {

    private final OperationProcessor operationProcessor;
    private final OperationCreateService operationCreateService;

    public OperationCreateController(OperationProcessor operationProcessor, OperationCreateService operationCreateService) {
        this.operationProcessor = operationProcessor;
        this.operationCreateService = operationCreateService;
    }

    // DUAL-WRITE: DB commit + Kafka publish as two separate, non-atomic operations.
    // See OperationProcessor for a detailed explanation of the consistency risks.
    @Post("/v1")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV1(@Body @Valid CreateOperationRequest request) {
        return operationProcessor.process(request);
    }

    // OUTBOX: operation + outbox event written in a single DB transaction.
    // Kafka publish is handled asynchronously by a relay — not by this request.
    // See OperationCreateService#createWithOutbox for the transaction boundary.
    @Post("/v2")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV2(@Body @Valid CreateOperationRequest request) {
        return operationCreateService.createWithOutbox(request);
    }

}
