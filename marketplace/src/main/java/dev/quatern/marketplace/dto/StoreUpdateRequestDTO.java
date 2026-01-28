package dev.quatern.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreUpdateRequestDTO(
    @NotNull Store store
) {

    public record Store(
        @NotBlank String name,
        @NotBlank String callbackUrl
    ) {}

}
