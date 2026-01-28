package dev.quatern.marketplace.dto;

import dev.quatern.marketplace.model.Store;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreCreateRequestDTOMapper {

    Store toEntity(StoreCreateRequestDTO.Store dto);

}