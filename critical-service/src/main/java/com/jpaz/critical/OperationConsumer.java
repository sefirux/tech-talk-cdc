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

    // /v1 (dual-write), /v2 (outbox + polling relay), /v4 (outbox + CDC):
    // all three publish the same OperationResponse JSON to this topic.
    @Topic("operations.created")
    public void created(JsonNode payload) {
        log.info("Operation created: [{}]", payload.toPrettyString());
    }

    @Topic("operations.updated")
    public void updated(JsonNode payload) {
        log.info("Operation updated: [{}]", payload.toPrettyString());
    }

    // /v3 (CDC pure): Debezium publishes WAL changes to this topic as a Debezium
    // envelope (op + before/after). The table sync to critical_db is handled entirely
    // by the JDBC Sink connector — no application code involved.
    @Topic("cdc.public.operations")
    public void cdcOperation(JsonNode envelope) {
        log.info("CDC operation [op={}]: [{}]", envelope.path("op").asText(), envelope.path("after").toPrettyString());
    }

}
