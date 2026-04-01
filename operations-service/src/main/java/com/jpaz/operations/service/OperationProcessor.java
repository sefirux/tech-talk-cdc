package com.jpaz.operations.service;

import com.jpaz.operations.dto.CreateOperationRequest;
import com.jpaz.operations.dto.OperationResponse;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// PATRÓN ESCRITURA DUAL
//
// Este procesador coordina dos escrituras independientes:
//   1. Una escritura transaccional en la base de datos (via OperationCreateService)
//   2. Una publicación de mensaje en Kafka (via OperationProducer)
//
// Estos dos sistemas no comparten transacción. No existe ningún protocolo de coordinación
// (como 2PC) entre ellos. Esto genera una ventana de inconsistencia:
//
//   [commit en BD] ---- falla aquí ---- [publicación en Kafka]
//
// Si el proceso se cae, el broker es inalcanzable, o Kafka rechaza el mensaje
// después de que la base de datos ya confirmó, el evento se pierde definitivamente.
// El critical-service nunca sabrá que esta operación fue creada.
//
// A la inversa, si la publicación en Kafka tiene éxito pero el commit en la BD
// se revirtió en un escenario de reintento, los consumidores podrían procesar
// un evento fantasma sin registro correspondiente en la BD.
//
// Este es el problema central del patrón de escritura dual.
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
        // ESCRITURA 1: persistida atómicamente dentro de una transacción de base de datos.
        // Una vez que retorna, el registro está confirmado — no hay vuelta atrás.
        OperationResponse response = operationCreateService.create(request);

        // ESCRITURA 2: publicación fire-and-forget en Kafka, ocurre DESPUÉS del commit en la BD.
        // Sin garantía de atomicidad con la escritura anterior. Si falla, tenemos un
        // registro en la BD sin el evento correspondiente en el broker.
        operationProducer.sendCreated(response);

        log.info("Operation processed [{}]", response);

        return response;
    }
}
