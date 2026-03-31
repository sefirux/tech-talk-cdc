package com.jpaz.operations.service;

import com.jpaz.operations.dto.CreateOperationRequest;
import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.exception.AccountNotFoundException;
import com.jpaz.operations.exception.AssetNotFoundException;
import com.jpaz.operations.model.EventTypes;
import com.jpaz.operations.model.Operation;
import com.jpaz.operations.model.OperationStatus;
import com.jpaz.operations.repository.AccountRepository;
import com.jpaz.operations.repository.AssetRepository;
import com.jpaz.operations.repository.OperationRepository;
import com.jpaz.operations.service.mappers.OperationResponseMapper;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OperationCreateService {
    private static final Logger log = LoggerFactory.getLogger(OperationCreateService.class);

    private final AccountRepository accountRepository;
    private final AssetRepository assetRepository;
    private final OperationRepository operationRepository;
    private final OutboxService outboxService;

    public OperationCreateService(AccountRepository accountRepository, AssetRepository assetRepository, OperationRepository operationRepository, OutboxService outboxService) {
        this.accountRepository = accountRepository;
        this.assetRepository = assetRepository;
        this.operationRepository = operationRepository;
        this.outboxService = outboxService;
    }

    // Used by the dual-write path (/v1).
    // The @Transactional boundary ends here — the record is committed before this method
    // returns. Whatever happens after this call (e.g., publishing to Kafka in
    // OperationProcessor) is outside this transaction and therefore not atomic with it.
    @Transactional
    public OperationResponse create(CreateOperationRequest request) {
        if (!accountRepository.existsById(request.accountId())) {
            throw new AccountNotFoundException(request.accountId());
        }
        if (!assetRepository.existsById(request.assetId())) {
            throw new AssetNotFoundException(request.assetId());
        }

        Operation operation = new Operation();
        operation.setAccountId(request.accountId());
        operation.setAssetId(request.assetId());
        operation.setSide(request.side());
        operation.setQuantity(request.quantity());
        operation.setPrice(request.price());
        operation.setStatus(OperationStatus.PENDING);

        operation = operationRepository.save(operation);
        var response = OperationResponseMapper.map(operation);

        log.info("Operation persisted [{}]", response);

        return response;
    }

    // Used by the outbox path (/v2).
    // This single transaction writes both the operation record and the outbox event
    // atomically. Either both are committed or neither is — the DB guarantees this.
    //
    // The Kafka publication is decoupled entirely from this call: a relay process will
    // later read pending outbox events and publish them. The application no longer
    // writes directly to two systems at the same time.
    //
    // Note: calling create() here is an internal (non-proxied) call, so its own
    // @Transactional annotation is a no-op. This method's transaction is the one
    // that controls the boundary for both writes.
    @Transactional
    public OperationResponse createWithOutbox(CreateOperationRequest request) {
        OperationResponse response = create(request);
        outboxService.saveCreatedEvent(EventTypes.OPERATION_CREATED, response.id(), response);
        return response;
    }

}
