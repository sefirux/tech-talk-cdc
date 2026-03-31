package com.jpaz.operations.repository;

import com.jpaz.operations.model.Asset;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AssetRepository extends PageableRepository<Asset, UUID> {
}
