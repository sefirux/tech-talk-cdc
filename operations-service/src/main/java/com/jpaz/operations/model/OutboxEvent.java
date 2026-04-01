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

    // ID de la entidad de dominio que originó este evento (ej. el ID de la operación).
    private UUID aggregateId;

    // Identifica qué ocurrió (ej. "operation.created").
    // El relay usa este campo para enrutar el evento al topic de Kafka correcto.
    private String eventType;

    // Payload serializado en JSON. Se almacena como texto para que la tabla outbox
    // sea agnóstica al esquema — no necesita conocer los tipos de dominio.
    private String payload;

    @DateCreated
    private OffsetDateTime createdAt;

    // Null hasta que el relay publique exitosamente el evento en Kafka.
    // El relay actualiza este campo para indicar que el evento fue entregado.
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
