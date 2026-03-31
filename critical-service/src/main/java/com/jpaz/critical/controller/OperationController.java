package com.jpaz.critical.controller;

import com.jpaz.critical.model.Operation;
import com.jpaz.critical.repository.OperationRepository;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.List;

@Controller("/operations")
public class OperationController {

    private final OperationRepository operationRepository;

    public OperationController(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Get
    public List<Operation> findAll() {
        return operationRepository.findAll();
    }
}
