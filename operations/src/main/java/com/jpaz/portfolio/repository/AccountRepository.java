package com.jpaz.portfolio.repository;

import com.jpaz.portfolio.model.Account;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface AccountRepository extends CrudRepository<Account, UUID> {
}
