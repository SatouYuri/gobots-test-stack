package dev.quatern.receiver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.quatern.receiver.dto.EventListenRequestDTO;
import dev.quatern.receiver.dto.EventListenRequestDTOMapper;
import dev.quatern.receiver.model.Event;
import dev.quatern.receiver.service.EventService;
import dev.quatern.receiver.enums.EventSubjectTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EventListenRequestDTOMapper eventListenRequestDTOMapper;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private Event sampleEvent;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .setValidator(validator)
            .build();

        sampleEvent = new Event();
        sampleEvent.setId("event-1");
        sampleEvent.setReceivedAt(LocalDateTime.of(2023,1,1,0,0));
        sampleEvent.setType("ORDER_CREATED");
        sampleEvent.setSubjectType(EventSubjectTypeEnum.ORDER);
        sampleEvent.setMarketplaceStoreId("store-1");
        sampleEvent.setMarketplaceSubjectId("order-1");
    }

    @Test
    void listen_shouldReturnRegisteredResponse() throws Exception {
        EventListenRequestDTO.Event dto = new EventListenRequestDTO.Event(
            "ORDER_CREATED",
            "ORDER",
            "store-1",
            "order-1"
        );
        EventListenRequestDTO requestDTO = new EventListenRequestDTO(dto);

        when(eventListenRequestDTOMapper.toEntity(any())).thenReturn(sampleEvent);
        when(eventService.create(any())).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            if (e.getId() == null) e.setId("event-1");
            return e;
        });

        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", is("Event successfully registered")))
            .andExpect(jsonPath("$.eventId", is(sampleEvent.getId())));

        verify(eventListenRequestDTOMapper, times(1)).toEntity(any());
        verify(eventService, times(1)).create(any());
    }

    @Test
    void listen_shouldReturnBadRequestOnInvalidPayload() throws Exception {
        // Send malformed JSON so Jackson fails to deserialize and request fails with 400
        String invalid = "{"; // malformed JSON

        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalid))
            .andExpect(status().isBadRequest());

        // mapper and service should not be invoked when body is invalid
        verifyNoInteractions(eventListenRequestDTOMapper);
        verifyNoInteractions(eventService);
    }

}
