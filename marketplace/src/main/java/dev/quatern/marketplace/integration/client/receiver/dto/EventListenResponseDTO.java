package dev.quatern.marketplace.integration.client.receiver.dto;

public record EventListenResponseDTO(
    String message,
    String eventId
) {}
