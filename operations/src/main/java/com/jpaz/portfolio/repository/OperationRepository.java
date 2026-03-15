package com.jpaz.portfolio.repository;

import com.jpaz.portfolio.model.Operation;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface OperationRepository extends PageableRepository<Operation, UUID> {

    Page<Operation> findByAccountId(UUID accountId, Pageable pageable);
}
