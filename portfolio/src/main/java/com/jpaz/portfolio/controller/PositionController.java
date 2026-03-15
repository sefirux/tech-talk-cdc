package com.jpaz.portfolio.controller;

import com.jpaz.portfolio.dto.CreatePositionRequest;
import com.jpaz.portfolio.dto.PositionResponse;
import com.jpaz.portfolio.dto.UpdatePositionRequest;
import com.jpaz.portfolio.service.PositionService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import jakarta.validation.Valid;

import java.util.UUID;

@Controller("/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @Get
    public Page<PositionResponse> list(Pageable pageable) {
        return positionService.findAll(pageable);
    }

    @Get("/{id}")
    public PositionResponse get(UUID id) {
        return positionService.findById(id);
    }

    @Get("/account/{accountId}")
    public Page<PositionResponse> listByAccount(UUID accountId, Pageable pageable) {
        return positionService.findByAccountId(accountId, pageable);
    }

    @Post
    @Status(HttpStatus.CREATED)
    public PositionResponse create(@Body @Valid CreatePositionRequest request) {
        return positionService.create(request);
    }

    @Put("/{id}")
    public PositionResponse update(UUID id, @Body @Valid UpdatePositionRequest request) {
        return positionService.update(id, request);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(UUID id) {
        positionService.delete(id);
    }
}
