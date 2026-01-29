package dev.quatern.receiver.dto;

public record EventListenResponseDTO(
    String message,
    String eventId
) {}
