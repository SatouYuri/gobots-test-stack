package dev.quatern.marketplace.dto;

import dev.quatern.marketplace.model.Store;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreGetResponseDTOMapper {

    StoreGetResponseDTO.Store toDTO(Store store);

}
