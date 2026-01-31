package dev.quatern.marketplace.integration.client.receiver;

import dev.quatern.marketplace.integration.client.receiver.dto.EventListenRequestDTO;
import dev.quatern.marketplace.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiverClientService {

    private final WebClient.Builder webClientBuilder;

    public void sendOrder(Order order) { //TODO: Adicionar logs nessa classe toda...
        try {
            EventListenRequestDTO requestBody = new EventListenRequestDTO(
                new EventListenRequestDTO.Event(
                    "order." + order.getStatus().name().toLowerCase(),
                    "ORDER",
                    order.getStore().getId(),
                    order.getId()
                )
            );
            try {
                webClientBuilder
                    .baseUrl(order.getStore().getCallbackUrl())
                    .defaultHeader("Content-Type", "application/json")
                    .build()
                    .post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            } catch (WebClientResponseException e) {
                //TODO: Adicionar logs nessa classe toda...
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            //TODO: Adicionar logs nessa classe toda...
            throw new RuntimeException(e);
        }
    }

}
