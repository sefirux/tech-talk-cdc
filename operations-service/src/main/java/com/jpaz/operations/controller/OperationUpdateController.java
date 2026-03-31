package com.jpaz.operations.controller;

import com.jpaz.operations.dto.OperationResponse;
import com.jpaz.operations.dto.UpdateOperationRequest;
import com.jpaz.operations.service.OperationUpdateService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Put;
import jakarta.validation.Valid;

import java.util.UUID;

@Controller
public class OperationUpdateController {

    private final OperationUpdateService updateService;

    public OperationUpdateController(OperationUpdateService updateService) {
        this.updateService = updateService;
    }

    @Put(value = "/operations/{id}", produces = "application/json")
    public OperationResponse update(UUID id, @Body @Valid UpdateOperationRequest request) {
        return updateService.update(id, request);
    }

}
