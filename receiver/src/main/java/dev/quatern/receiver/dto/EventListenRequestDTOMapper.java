package dev.quatern.receiver.dto;

import dev.quatern.receiver.model.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventListenRequestDTOMapper {

    Event toEntity(EventListenRequestDTO.Event dto);

}
