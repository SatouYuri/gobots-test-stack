package dev.quatern.receiver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventListenRequestDTO(
    @NotNull Event event
) {

    public record Event(
        @NotBlank String type,
        @NotBlank String subjectType,
        @NotBlank String marketplaceStoreId,
        @NotBlank String marketplaceSubjectId
    ) {}

}
