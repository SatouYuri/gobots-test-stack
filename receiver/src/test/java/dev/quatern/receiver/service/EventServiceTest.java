package dev.quatern.receiver.service;

import dev.quatern.receiver.integration.client.receiver.MarketplaceClientService;
import dev.quatern.receiver.model.Event;
import dev.quatern.receiver.repository.EventRepository;
import dev.quatern.receiver.enums.EventSubjectTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MarketplaceClientService marketplaceClientService;

    @InjectMocks
    private EventService eventService;

    private Event sampleEvent;

    @BeforeEach
    void setup() {
        sampleEvent = new Event();
        sampleEvent.setId("event-1");
        sampleEvent.setReceivedAt(LocalDateTime.of(2023,1,1,0,0));
        sampleEvent.setType("ORDER_UPDATED");
        sampleEvent.setSubjectType(EventSubjectTypeEnum.ORDER);
        sampleEvent.setMarketplaceStoreId("store-1");
        sampleEvent.setMarketplaceSubjectId("order-1");
    }

    @Test
    void create_shouldReturnExistingEvent_whenAlreadyRegistered() {
        when(eventRepository.findByTypeAndSubjectTypeAndMarketplaceStoreIdAndMarketplaceSubjectId(
            anyString(), eq(sampleEvent.getSubjectType()), anyString(), anyString()
        )).thenReturn(Optional.of(sampleEvent));

        Event result = eventService.create(sampleEvent);

        assertNotNull(result);
        assertEquals(sampleEvent, result);
        verify(eventRepository, never()).save(any(Event.class));
        verify(marketplaceClientService, never()).fetchSubjectAsEventSnapshot(any(Event.class));
    }

    @Test
    void create_shouldFetchSnapshotAndSave_whenNotRegisteredAndMarketplaceSubjectIdNotNull() {
        when(eventRepository.findByTypeAndSubjectTypeAndMarketplaceStoreIdAndMarketplaceSubjectId(
            anyString(), any(EventSubjectTypeEnum.class), anyString(), anyString()
        )).thenReturn(Optional.empty());

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.create(sampleEvent);

        assertNotNull(result);
        verify(marketplaceClientService, times(1)).fetchSubjectAsEventSnapshot(sampleEvent);
        verify(eventRepository, times(1)).save(sampleEvent);
    }

    @Test
    void create_shouldSaveWithoutFetching_whenMarketplaceSubjectIdIsNull() {
        sampleEvent.setMarketplaceSubjectId(null);

        when(eventRepository.findByTypeAndSubjectTypeAndMarketplaceStoreIdAndMarketplaceSubjectId(
            anyString(), any(EventSubjectTypeEnum.class), anyString(), isNull()
        )).thenReturn(Optional.empty());

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.create(sampleEvent);

        assertNotNull(result);
        verify(marketplaceClientService, never()).fetchSubjectAsEventSnapshot(any(Event.class));
        verify(eventRepository, times(1)).save(sampleEvent);
    }

}
