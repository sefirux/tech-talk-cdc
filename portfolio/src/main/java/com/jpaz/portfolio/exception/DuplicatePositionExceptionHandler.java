package com.jpaz.portfolio.exception;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

import java.util.Map;

@Singleton
@Produces
@Requires(classes = {DuplicatePositionException.class, ExceptionHandler.class})
public class DuplicatePositionExceptionHandler implements ExceptionHandler<DuplicatePositionException, HttpResponse<Map<String, String>>> {

    @Override
    public HttpResponse<Map<String, String>> handle(HttpRequest request, DuplicatePositionException e) {
        return HttpResponse.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
}
