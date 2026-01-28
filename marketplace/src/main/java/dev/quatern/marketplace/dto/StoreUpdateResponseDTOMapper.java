package dev.quatern.marketplace.dto;

import dev.quatern.marketplace.model.Store;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreUpdateResponseDTOMapper {

    StoreUpdateResponseDTO.Store toDTO(Store store);

}
