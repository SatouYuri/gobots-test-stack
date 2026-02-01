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
        return eventRepository.findByTypeAndSubjectTypeAndMarketplaceStoreIdAndMarketplaceSubjectId(
            event.getType(),
            event.getSubjectType(),
            event.getMarketplaceStoreId(),
            event.getMarketplaceSubjectId()
        )
        .orElseGet(() -> {
            if (event.getMarketplaceSubjectId() != null)
                marketplaceClientService.fetchSubjectAsEventSnapshot(event);
            return eventRepository.save(event);
        });
    }

}
