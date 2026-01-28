package dev.quatern.marketplace.dto;

import dev.quatern.marketplace.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderUpdateStatusResponseDTOMapper {

    OrderUpdateStatusResponseDTO.Order toDTO(Order order);

}
