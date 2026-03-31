package com.jpaz.operations.service;

import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.dto.UpdateOperationRequest;
import com.jpaz.operations.exception.OperationCannotBeUpdated;
import com.jpaz.operations.exception.OperationNotFoundException;
import com.jpaz.operations.model.Operation;
import com.jpaz.operations.repository.OperationRepository;
import com.jpaz.operations.service.mappers.OperationResponseMapper;
import jakarta.inject.Singleton;

import java.util.UUID;

@Singleton
public class OperationUpdateService {

    private final OperationRepository operationRepository;
    private final OperationProducer operationProducer;

    public OperationUpdateService(OperationRepository operationRepository, OperationProducer operationProducer) {
        this.operationRepository = operationRepository;
        this.operationProducer = operationProducer;
    }

    public OperationResponse update(UUID id, UpdateOperationRequest request) {
        Operation operation = operationRepository.findById(id)
                .orElseThrow(() -> new OperationNotFoundException(id));

        if (operation.isFinalStatus()) {
            throw new OperationCannotBeUpdated(operation.getId(), operation.getStatus(), request.status());
        }

        operation.setStatus(request.status());

        operation = operationRepository.update(operation);

        var response = OperationResponseMapper.map(operation);

        operationProducer.sendUpdated(response);

        return response;
    }

}
