package dev.quatern.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequestDTO(
    @NotBlank String storeId,
    @NotNull Order order
) {

    public record Order(
        String mock // Aqui seriam declarados os campos (correspondentes aos da entidade dev.quatern.marketplace.model.Order para fins de mapeamento via MapStruct) para a criação do pedido, tais como cliente, lista de itens, etc.
    ) {}

}
