package com.jpaz.portfolio.service;

import com.jpaz.portfolio.dto.CreateOperationRequest;
import com.jpaz.portfolio.dto.OperationResponse;
import com.jpaz.portfolio.dto.UpdateOperationRequest;
import com.jpaz.portfolio.exception.AccountNotFoundException;
import com.jpaz.portfolio.exception.AssetNotFoundException;
import com.jpaz.portfolio.exception.OperationNotFoundException;
import com.jpaz.portfolio.model.Operation;
import com.jpaz.portfolio.model.OperationStatus;
import com.jpaz.portfolio.repository.AccountRepository;
import com.jpaz.portfolio.repository.AssetRepository;
import com.jpaz.portfolio.repository.OperationRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Singleton
public class OperationService {

    private final OperationRepository operationRepository;
    private final AccountRepository accountRepository;
    private final AssetRepository assetRepository;

    public OperationService(OperationRepository operationRepository,
                            AccountRepository accountRepository,
                            AssetRepository assetRepository) {
        this.operationRepository = operationRepository;
        this.accountRepository = accountRepository;
        this.assetRepository = assetRepository;
    }

    public Page<OperationResponse> findAll(Pageable pageable) {
        return operationRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<OperationResponse> findByAccountId(UUID accountId, Pageable pageable) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        return operationRepository.findByAccountId(accountId, pageable).map(this::toResponse);
    }

    public OperationResponse findById(UUID id) {
        return operationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new OperationNotFoundException(id));
    }

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

        return toResponse(operationRepository.save(operation));
    }

    @Transactional
    public OperationResponse update(UUID id, UpdateOperationRequest request) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new OperationNotFoundException(id));

        operation.setStatus(request.status());

        return toResponse(operationRepository.update(operation));
    }

    @Transactional
    public void delete(UUID id) {
        if (!operationRepository.existsById(id)) {
            throw new OperationNotFoundException(id);
        }
        operationRepository.deleteById(id);
    }

    private OperationResponse toResponse(Operation op) {
        return new OperationResponse(
                op.getId(),
                op.getAccountId(),
                op.getAssetId(),
                op.getSide(),
                op.getQuantity(),
                op.getPrice(),
                op.getStatus(),
                op.getCreatedAt(),
                op.getUpdatedAt()
        );
    }
}
