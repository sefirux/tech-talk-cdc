package com.jpaz.operations.service;

import com.jpaz.operations.dto.AssetResponse;
import com.jpaz.operations.model.Asset;
import com.jpaz.operations.repository.AssetRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

@Singleton
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public Page<AssetResponse> findAll(Pageable pageable) {
        return assetRepository.findAll(pageable).map(this::toResponse);
    }

    private AssetResponse toResponse(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getName(),
                asset.getCreatedAt()
        );
    }
}
