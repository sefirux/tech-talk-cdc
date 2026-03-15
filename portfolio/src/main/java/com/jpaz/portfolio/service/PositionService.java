package com.jpaz.portfolio.service;

import com.jpaz.portfolio.dto.CreatePositionRequest;
import com.jpaz.portfolio.dto.PositionResponse;
import com.jpaz.portfolio.dto.UpdatePositionRequest;
import com.jpaz.portfolio.exception.DuplicatePositionException;
import com.jpaz.portfolio.exception.PositionNotFoundException;
import com.jpaz.portfolio.model.Position;
import com.jpaz.portfolio.repository.PositionRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Singleton
public class PositionService {

    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public Page<PositionResponse> findAll(Pageable pageable) {
        return positionRepository.findAll(pageable).map(this::toResponse);
    }

    public PositionResponse findById(UUID id) {
        return positionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new PositionNotFoundException(id));
    }

    public Page<PositionResponse> findByAccountId(UUID accountId, Pageable pageable) {
        return positionRepository.findByAccountId(accountId, pageable).map(this::toResponse);
    }

    @Transactional
    public PositionResponse create(CreatePositionRequest request) {
        if (positionRepository.existsByAccountIdAndAssetId(request.accountId(), request.assetId())) {
            throw new DuplicatePositionException(request.accountId(), request.assetId());
        }

        Position position = new Position();
        position.setAccountId(request.accountId());
        position.setAssetId(request.assetId());
        position.setQuantity(request.quantity());
        position.setAveragePrice(request.averagePrice());

        return toResponse(positionRepository.save(position));
    }

    @Transactional
    public PositionResponse update(UUID id, UpdatePositionRequest request) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException(id));

        position.setQuantity(request.quantity());
        position.setAveragePrice(request.averagePrice());

        return toResponse(positionRepository.update(position));
    }

    @Transactional
    public void delete(UUID id) {
        if (!positionRepository.existsById(id)) {
            throw new PositionNotFoundException(id);
        }
        positionRepository.deleteById(id);
    }

    private PositionResponse toResponse(Position p) {
        return new PositionResponse(
                p.getId(),
                p.getAccountId(),
                p.getAssetId(),
                p.getQuantity(),
                p.getAveragePrice(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
