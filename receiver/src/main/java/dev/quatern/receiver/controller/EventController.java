package dev.quatern.receiver.controller;

import dev.quatern.receiver.dto.EventListenRequestDTO;
import dev.quatern.receiver.dto.EventListenRequestDTOMapper;
import dev.quatern.receiver.dto.EventListenResponseDTO;
import dev.quatern.receiver.model.Event;
import dev.quatern.receiver.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventListenRequestDTOMapper eventListenRequestDTOMapper;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventListenResponseDTO> listen(@RequestBody @Valid EventListenRequestDTO requestDTO) {
        Event event = eventService.createOrderEvent(eventListenRequestDTOMapper.toEntity(requestDTO.event()));
        return ResponseEntity.ok().body(
            new EventListenResponseDTO(
                "Order event successfully registered",
                event.getId()
            )
        );
    }

}
