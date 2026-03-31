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

    // OUTBOX + POLLING RELAY: operation + outbox event written in a single DB transaction.
    // The OutboxRelayJob polls the outbox table every 5s and publishes to Kafka.
    // See OperationCreateService#createWithOutbox for the transaction boundary.
    @Post("/v2")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV2(@Body @Valid CreateOperationRequest request) {
        return operationCreateService.createWithOutbox(request);
    }

    // CDC: the app only writes to the DB — no Kafka publish from application code.
    // Debezium reads the WAL and publishes the operations table changes to Kafka
    // automatically. Requires the v3 connector to be registered (see connectors/v3-operations-cdc.json).
    // The event lands on topic: cdc.public.operations (Debezium envelope format).
    @Post("/v3")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV3(@Body @Valid CreateOperationRequest request) {
        return operationCreateService.create(request);
    }

    // CDC + OUTBOX: same DB writes as /v2, but the relay is Debezium — not the polling job.
    // Debezium reads inserts on outbox_events from the WAL and the Outbox Event Router SMT
    // routes each event to its Kafka topic (operations.created, operations.updated, ...).
    // Requires the v4 connector (connectors/v4-outbox-cdc.json) and outbox.relay.enabled=false.
    // The event lands on the same topic as /v1 and /v2: operations.created.
    @Post("/v4")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV4(@Body @Valid CreateOperationRequest request) {
        return operationCreateService.createWithOutbox(request);
    }

}
