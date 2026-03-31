package com.jpaz.operations.model;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

@MappedEntity("outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue
    private UUID id;

    // ID of the domain entity that originated this event (e.g. the operation ID).
    private UUID aggregateId;

    // Identifies what happened (e.g. "operation.created").
    // The relay uses this to route the event to the correct Kafka topic.
    private String eventType;

    // JSON-serialized payload. Stored as text so the outbox table stays
    // schema-agnostic — it doesn't need to know about domain types.
    private String payload;

    @DateCreated
    private OffsetDateTime createdAt;

    // Null until the relay successfully publishes the event to Kafka.
    // The relay sets this field to signal that the event has been delivered.
    private OffsetDateTime processedAt;

    public OutboxEvent() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAggregateId() { return aggregateId; }
    public void setAggregateId(UUID aggregateId) { this.aggregateId = aggregateId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(OffsetDateTime processedAt) { this.processedAt = processedAt; }
}
