package com.jpaz.operations.service;

import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.dto.UpdateOperationRequest;
import com.jpaz.operations.exception.OperationNotFoundException;
import com.jpaz.operations.model.Operation;
import com.jpaz.operations.repository.OperationRepository;
import com.jpaz.operations.service.mappers.OperationResponseMapper;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Singleton
public class OperationUpdateService {

    private final OperationRepository operationRepository;

    public OperationUpdateService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Transactional
    public OperationResponse update(UUID id, UpdateOperationRequest request) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new OperationNotFoundException(id));

        operation.setStatus(request.status());

        return OperationResponseMapper.map(operationRepository.update(operation));
    }

}
