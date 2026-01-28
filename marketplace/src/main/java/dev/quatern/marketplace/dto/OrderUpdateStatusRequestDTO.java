package dev.quatern.marketplace.dto;

import dev.quatern.marketplace.enums.OrderStatusEnum;
import jakarta.validation.constraints.NotNull;

public record OrderUpdateStatusRequestDTO(
    @NotNull OrderStatusEnum status
) {}
