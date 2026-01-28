package dev.quatern.marketplace.dto;

import java.time.LocalDateTime;

public record StoreCreateResponseDTO(
    String message,
    Store store
) {

    public record Store(
        String id,
        String name,
        String callbackUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

}
