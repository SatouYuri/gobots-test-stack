package dev.quatern.receiver.service;

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

    public Event createOrderEvent(Event event) {
        Event persistedEvent = eventRepository.save(event);
        //TODO: Consultar a API do Marketplace pra obter dados do pedido e salvar como snapshot JSON no evento
        event.setSubjectSnapshot("MOCK"); //TODO: Consultar a API do Marketplace pra obter dados do pedido e salvar como snapshot JSON no evento
        return persistedEvent;
    }

}
