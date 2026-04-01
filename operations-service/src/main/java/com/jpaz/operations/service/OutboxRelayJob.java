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

// Relay que conecta la tabla outbox con Kafka.
//
// Es la segunda mitad del patrón outbox. Consulta los eventos que fueron
// confirmados en la BD (via OperationCreateService#createWithOutbox) pero aún no
// publicados en Kafka, los publica y los marca como procesados.
//
// Garantía de entrega: al menos una vez (at-least-once).
// El evento se marca como procesado solo DESPUÉS de una publicación exitosa. Si el proceso
// falla entre la publicación y la llamada a markAsProcessed, el evento se
// volverá a publicar en el próximo ciclo de polling. Los consumidores deben ser idempotentes.
//
// Cada evento se maneja de forma independiente — un fallo en uno no bloquea al resto.
//
// Este bean solo se crea cuando outbox.relay.enabled=true (application.properties).
// Al usar CDC (v3/v4), poner en false: Debezium lee la tabla outbox desde el
// WAL y publica en Kafka directamente, por lo que este relay por polling no debe ejecutarse en paralelo.
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
                // Registrar y continuar — no dejar que un evento fallido bloquee al resto.
                // El evento se reintentará en el próximo ciclo de polling ya que processedAt
                // permanece null.
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
