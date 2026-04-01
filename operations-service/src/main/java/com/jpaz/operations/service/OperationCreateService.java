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

    // Utilizado por el flujo de escritura dual (/v1).
    // El límite @Transactional termina aquí — el registro se confirma antes de que este método
    // retorne. Todo lo que ocurra después de esta llamada (ej. publicar en Kafka desde
    // OperationProcessor) está fuera de esta transacción y por lo tanto no es atómico con ella.
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

    // Utilizado por el flujo de outbox (/v2).
    // Esta única transacción escribe tanto el registro de la operación como el evento de outbox
    // de forma atómica. O ambos se confirman o ninguno — la BD lo garantiza.
    //
    // La publicación en Kafka está completamente desacoplada de esta llamada: un proceso relay
    // leerá luego los eventos de outbox pendientes y los publicará. La aplicación ya no
    // escribe directamente en dos sistemas al mismo tiempo.
    //
    // Nota: llamar a create() aquí es una llamada interna (sin proxy), por lo que su propia
    // anotación @Transactional no tiene efecto. La transacción de este método es la que
    // controla el límite para ambas escrituras.
    @Transactional
    public OperationResponse createWithOutbox(CreateOperationRequest request) {
        OperationResponse response = create(request);
        outboxService.saveCreatedEvent(EventTypes.OPERATION_CREATED, response.id(), response);
        return response;
    }

}
