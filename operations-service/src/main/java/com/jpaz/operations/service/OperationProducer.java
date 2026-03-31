package com.jpaz.operations.service;

import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.model.EventTypes;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.inject.Singleton;

@Singleton
@KafkaClient
public interface OperationProducer {

    @Topic(EventTypes.OPERATION_CREATED)
    void sendCreated(OperationResponse operation);

    @Topic(EventTypes.OPERATION_UPDATED)
    void sendUpdated(OperationResponse response);
}
