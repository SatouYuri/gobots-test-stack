package dev.quatern.marketplace.dto;

import dev.quatern.marketplace.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderGetResponseDTOMapper {

    OrderGetResponseDTO.Order toDTO(Order order);

}
