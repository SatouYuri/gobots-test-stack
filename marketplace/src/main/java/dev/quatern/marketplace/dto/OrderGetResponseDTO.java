package dev.quatern.marketplace.dto;

import java.time.LocalDateTime;

public record OrderGetResponseDTO(
    String message,
    Order order
) {

    public record Order(
        String id,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

}