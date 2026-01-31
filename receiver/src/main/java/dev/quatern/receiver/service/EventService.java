package dev.quatern.receiver.service;

import dev.quatern.receiver.integration.client.receiver.MarketplaceClientService;
import dev.quatern.receiver.model.Event;
import dev.quatern.receiver.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService extends BaseService<Event> {

    private final EventRepository eventRepository;
    private final MarketplaceClientService marketplaceClientService;

    public Event create(Event event) {
        marketplaceClientService.pullUpdateSubjectAsEventSnapshot(event); //TODO: Cercar isso com try-catch e tratamento com job depois em caso de falha...
        return eventRepository.save(event);
    }

}
