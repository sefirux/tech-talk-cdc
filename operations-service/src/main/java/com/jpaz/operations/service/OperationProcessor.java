package com.jpaz.operations.service;

import com.jpaz.operations.dto.CreateOperationRequest;
import com.jpaz.operations.dto.OperationResponse;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// DUAL-WRITE PATTERN
//
// This processor orchestrates two independent writes:
//   1. A transactional writing to the database (via OperationCreateService)
//   2. A message publication to Kafka (via OperationProducer)
//
// These two systems have no shared transaction. There is no coordination protocol
// (like 2PC) between them. This creates a consistency window:
//
//   [DB commit] ---- failure here ---- [Kafka publish]
//
// If the process crashes, the broker is unreachable, or Kafka rejects the message
// after the database has already committed, the event is lost permanently.
// The critical-service will never know this operation was created.
//
// Conversely, if somehow the Kafka publication succeeds but the database commit was
// already done and rolled back in a retry scenario, consumers could process a
// phantom event with no corresponding record in the DB.
//
// This is the core problem of the dual-write pattern.
@Singleton
public class OperationProcessor {
    private static final Logger log = LoggerFactory.getLogger(OperationProcessor.class);

    private final OperationCreateService operationCreateService;
    private final OperationProducer operationProducer;

    public OperationProcessor(OperationCreateService operationCreateService, OperationProducer operationProducer) {
        this.operationCreateService = operationCreateService;
        this.operationProducer = operationProducer;
    }

    public OperationResponse process(CreateOperationRequest request) {
        // WRITE 1: persisted atomically within a database transaction.
        // Once this returns, the record is committed — there is no going back.
        OperationResponse response = operationCreateService.create(request);

        // WRITE 2: fire-and-forget publish to Kafka, happening AFTER the DB commit.
        // No atomicity guarantee with the writing above. If this fails, we have a
        // record in the DB with no corresponding event in the broker.
        operationProducer.sendCreated(response);

        log.info("Operation processed [{}]", response);

        return response;
    }
}
