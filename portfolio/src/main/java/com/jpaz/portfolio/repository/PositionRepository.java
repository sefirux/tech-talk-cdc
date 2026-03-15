package com.jpaz.portfolio.repository;

import com.jpaz.portfolio.model.Position;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PositionRepository extends PageableRepository<Position, UUID> {

    Page<Position> findByAccountId(UUID accountId, Pageable pageable);

    Optional<Position> findByAccountIdAndAssetId(UUID accountId, UUID assetId);

    boolean existsByAccountIdAndAssetId(UUID accountId, UUID assetId);
}
