package dev.quatern.marketplace.dto;

import dev.quatern.marketplace.model.Store;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface StoreUpdateRequestDTOMapper {

    Store toEntity(StoreUpdateRequestDTO.Store dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void map(StoreUpdateRequestDTO.Store source, @MappingTarget Store target);

}