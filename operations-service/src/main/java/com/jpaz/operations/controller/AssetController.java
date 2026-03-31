package com.jpaz.operations.controller;

import com.jpaz.operations.dto.AssetResponse;
import com.jpaz.operations.service.AssetService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @Get
    public Page<AssetResponse> list(Pageable pageable) {
        return assetService.findAll(pageable);
    }
}
