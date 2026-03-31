package com.jpaz.operations.service;

import com.jpaz.operations.dto.CreateOperationRequest;
import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.exception.AccountNotFoundException;
import com.jpaz.operations.exception.AssetNotFoundException;
import com.jpaz.operations.model.Operation;
import com.jpaz.operations.model.OperationStatus;
import com.jpaz.operations.repository.AccountRepository;
import com.jpaz.operations.repository.AssetRepository;
import com.jpaz.operations.repository.OperationRepository;
import com.jpaz.operations.service.mappers.OperationResponseMapper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OperationCreateService {
    private static final Logger log = LoggerFactory.getLogger(OperationCreateService.class);

    private final AccountRepository accountRepository;
    private final AssetRepository assetRepository;
    private final OperationProducer operationProducer;
    private final OperationRepository operationRepository;

    public OperationCreateService(AccountRepository accountRepository, AssetRepository assetRepository, OperationProducer operationProducer, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.assetRepository = assetRepository;
        this.operationProducer = operationProducer;
        this.operationRepository = operationRepository;
    }

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

        // transactional
        operation = operationRepository.save(operation);
        var response = OperationResponseMapper.map(operation);

        // send event
        operationProducer.sendCreated(response);

        log.info("Operation created [{}]", response);

        return response;
    }

}
