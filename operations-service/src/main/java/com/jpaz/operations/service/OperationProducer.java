package com.jpaz.operations.service;

import com.jpaz.operations.dto.OperationResponse;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import jakarta.inject.Singleton;

@Singleton
@KafkaClient
public interface OperationProducer {

    @Topic("operations.created")
    void sendCreated(OperationResponse operation);

}
