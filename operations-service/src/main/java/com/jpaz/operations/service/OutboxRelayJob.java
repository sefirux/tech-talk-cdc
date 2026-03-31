package com.jpaz.operations.service;

import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.model.EventTypes;
import com.jpaz.operations.model.OutboxEvent;
import com.jpaz.operations.repository.OutboxEventRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

// Relay that bridges the outbox table with Kafka.
//
// This is the second half of the outbox pattern. It polls for events that were
// committed to the DB (via OperationCreateService#createWithOutbox) but not yet
// published to Kafka, publishes them, and marks them as processed.
//
// Delivery guarantee: at-least-once.
// The event is marked as processed only AFTER a successful publication. If the process
// crashes between the publication and the markAsProcessed call, the event will be
// re-published in the next poll cycle. Consumers must be idempotent.
//
// Each event is handled independently — a failure on one does not block the rest.
//
// This bean is only created when outbox.relay.enabled=true (application.properties).
// When using CDC (v3/v4), set it to false: Debezium reads the outbox table from the
// WAL and publishes to Kafka directly, so this polling relay must not run in parallel.
@Singleton
@Requires(property = "outbox.relay.enabled", value = "true")
public class OutboxRelayJob {
    private static final Logger log = LoggerFactory.getLogger(OutboxRelayJob.class);

    private final OutboxEventRepository outboxEventRepository;
    private final OperationProducer operationProducer;
    private final ObjectMapper objectMapper;

    public OutboxRelayJob(OutboxEventRepository outboxEventRepository, OperationProducer operationProducer, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.operationProducer = operationProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = "5s")
    void relay() {
        List<OutboxEvent> pending = outboxEventRepository.findPending();

        if (pending.isEmpty()) {
            return;
        }

        log.info("Relaying {} outbox event(s)", pending.size());

        for (OutboxEvent event : pending) {
            try {
                publish(event);
                outboxEventRepository.markAsProcessed(event.getId());
                log.info("Outbox event relayed [id={}, eventType={}]", event.getId(), event.getEventType());
            } catch (Exception e) {
                // Log and continue — don't let one failing event block the rest.
                // The event will be retried in the next poll cycle since processedAt
                // remains null.
                log.error("Failed to relay outbox event [id={}, eventType={}]", event.getId(), event.getEventType(), e);
            }
        }
    }

    private void publish(OutboxEvent event) throws IOException {
        OperationResponse payload = objectMapper.readValue(event.getPayload(), OperationResponse.class);

        switch (event.getEventType()) {
            case EventTypes.OPERATION_CREATED -> operationProducer.sendCreated(payload);
            case EventTypes.OPERATION_UPDATED -> operationProducer.sendUpdated(payload);
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
        }
    }
}
