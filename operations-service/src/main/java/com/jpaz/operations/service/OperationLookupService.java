package com.jpaz.operations.service;

import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.exception.AccountNotFoundException;
import com.jpaz.operations.exception.OperationNotFoundException;
import com.jpaz.operations.repository.AccountRepository;
import com.jpaz.operations.repository.OperationRepository;
import com.jpaz.operations.service.mappers.OperationResponseMapper;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import java.time.OffsetDateTime;
import java.util.UUID;

@Singleton
public class OperationLookupService {

    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public OperationLookupService(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    public Page<OperationResponse> findAll(Pageable pageable) {
        return operationRepository.findAll(pageable).map(OperationResponseMapper::map);
    }

    public Page<OperationResponse> findSince(OffsetDateTime since, Pageable pageable) {
        return operationRepository.findSince(since, pageable).map(OperationResponseMapper::map);
    }

    public Page<OperationResponse> findByAccountId(UUID accountId, Pageable pageable) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        return operationRepository.findByAccountId(accountId, pageable).map(OperationResponseMapper::map);
    }

    public OperationResponse findById(UUID id) {
        return operationRepository.findById(id)
                .map(OperationResponseMapper::map)
                .orElseThrow(() -> new OperationNotFoundException(id));
    }
}
