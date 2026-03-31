package com.jpaz.operations.repository;

import com.jpaz.operations.model.OutboxEvent;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OutboxEventRepository extends CrudRepository<OutboxEvent, UUID> {

    @Query("SELECT * FROM outbox_events WHERE processed_at IS NULL ORDER BY created_at ASC LIMIT 100")
    List<OutboxEvent> findPending();

    @Query("UPDATE outbox_events SET processed_at = NOW() WHERE id = :id")
    void markAsProcessed(UUID id);
}
