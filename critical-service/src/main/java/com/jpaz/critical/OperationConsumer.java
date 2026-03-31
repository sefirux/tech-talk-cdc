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

    @Topic("operations.created")
    public void accept(JsonNode payload) {
        log.info("Event received: [{}]", payload.toPrettyString());
    }

}
