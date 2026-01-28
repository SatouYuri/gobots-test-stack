package dev.quatern.marketplace.dto;

import java.time.LocalDateTime;

public record OrderCreateResponseDTO(
    String message,
    Order order
) {

    public record Order(
        String id,
        LocalDateTime createdAt
    ) {}

}
