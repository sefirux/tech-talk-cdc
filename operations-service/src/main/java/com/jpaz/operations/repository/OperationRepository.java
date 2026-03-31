package com.jpaz.operations.repository;

import com.jpaz.operations.model.Operation;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OperationRepository extends PageableRepository<Operation, UUID> {

    Page<Operation> findByAccountId(UUID accountId, Pageable pageable);

    @Query(
            value = "SELECT * FROM operations WHERE (created_at >= :since OR updated_at >= :since) ORDER BY created_at ASC",
            countQuery = "SELECT count(*) FROM operations WHERE (created_at >= :since OR updated_at >= :since)"
    )
    Page<Operation> findSince(OffsetDateTime since, Pageable pageable);
}
