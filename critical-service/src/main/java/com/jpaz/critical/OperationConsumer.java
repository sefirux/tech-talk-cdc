package com.jpaz.critical;

import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@KafkaListener
public class OperationConsumer {
    private static final Logger log = LoggerFactory.getLogger(OperationConsumer.class);

    // /v1 (escritura dual), /v2 (outbox + relay por polling), /v4 (outbox + CDC):
    // los tres publican el mismo JSON OperationResponse en este topic.
    @Topic("operations.created")
    public void created(JsonNode payload) {
        log.info("Operation created: [{}]", payload.toPrettyString());
    }

    @Topic("operations.updated")
    public void updated(JsonNode payload) {
        log.info("Operation updated: [{}]", payload.toPrettyString());
    }

    // /v3 (solo CDC): Debezium publica los cambios del WAL en este topic.
    // El SMT ExtractNewRecordState elimina el envelope y deja solo los campos de la fila,
    // más el campo __op agregado (c=create, u=update, d=delete).
    // La sincronización a critical_db la maneja íntegramente el JDBC Sink connector.
    @Topic("cdc.public.operations")
    public void cdcOperation(JsonNode operation) {
        log.info("CDC operation [op={}]: [{}]", operation.path("__op").asText(), operation.toPrettyString());
    }

}
