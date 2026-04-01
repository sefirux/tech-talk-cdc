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

    // ESCRITURA DUAL: commit en la BD + publicación en Kafka como dos operaciones separadas y no atómicas.
    // Ver OperationProcessor para una explicación detallada de los riesgos de consistencia.
    @Post("/v1")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV1(@Body @Valid CreateOperationRequest request) {
        return operationProcessor.process(request);
    }

    // OUTBOX + RELAY POR POLLING: operación + evento de outbox escritos en una única transacción de BD.
    // El OutboxRelayJob consulta la tabla outbox cada 5s y publica en Kafka.
    // Ver OperationCreateService#createWithOutbox para el límite de transacción.
    @Post("/v2")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV2(@Body @Valid CreateOperationRequest request) {
        return operationCreateService.createWithOutbox(request);
    }

    // CDC: la app solo escribe en la BD — sin publicación en Kafka desde el código de aplicación.
    // Debezium lee el WAL y publica automáticamente los cambios de la tabla operations en Kafka.
    // Requiere el conector v3 registrado (ver connectors/v3-operations-cdc.json).
    // El evento llega al topic: cdc.public.operations (formato envelope de Debezium).
    @Post("/v3")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV3(@Body @Valid CreateOperationRequest request) {
        return operationCreateService.create(request);
    }

    // CDC + OUTBOX: mismas escrituras en BD que /v2, pero el relay es Debezium — no el job de polling.
    // Debezium lee las inserciones en outbox_events desde el WAL y el SMT Outbox Event Router
    // enruta cada evento a su topic de Kafka (operations.created, operations.updated, ...).
    // Requiere el conector v4 (connectors/v4-outbox-cdc.json) y outbox.relay.enabled=false.
    // El evento llega al mismo topic que /v1 y /v2: operations.created.
    @Post("/v4")
    @Status(HttpStatus.CREATED)
    public OperationResponse createV4(@Body @Valid CreateOperationRequest request) {
        return operationCreateService.createWithOutbox(request);
    }

}
