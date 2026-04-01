package com.jpaz.operations.service;

import com.jpaz.operations.model.OutboxEvent;
import com.jpaz.operations.repository.OutboxEventRepository;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

// Responsable únicamente de persistir eventos de outbox en la base de datos.
// Este servicio no tiene conocimiento de Kafka — solo escribe en la BD.
// El proceso relay (no implementado aquí) es el responsable de leer
// los eventos pendientes y publicarlos en el broker.
@Singleton
public class OutboxService {
    private static final Logger log = LoggerFactory.getLogger(OutboxService.class);

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public void saveCreatedEvent(String eventType, UUID eventId, Object payload) {
        OutboxEvent event = new OutboxEvent();
        event.setAggregateId(eventId);
        event.setEventType(eventType);
        event.setPayload(serialize(payload));

        outboxEventRepository.save(event);

        log.info("Outbox event saved [aggregateId={}, eventType={}]", event.getAggregateId(), event.getEventType());
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize outbox payload: " + payload, e);
        }
    }
}
