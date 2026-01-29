package dev.quatern.marketplace.integration.client.receiver.dto;

public record EventListenRequestDTO(
    Event event
) {

    public record Event(
        String type,
        String marketplaceStoreId,
        String marketplaceSubjectId
    ) {}

}
