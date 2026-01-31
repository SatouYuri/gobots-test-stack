package dev.quatern.marketplace.integration.client.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.quatern.marketplace.integration.client.receiver.dto.EventListenRequestDTO;
import dev.quatern.marketplace.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiverClientService {

    private static final String LOG_PREFIX = "[Receiver]";

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public void sendOrder(Order order) {
        log.info("{} Sending order with id {} as an event to be registered", LOG_PREFIX, order.getId());
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
                ResponseEntity<String> response = webClientBuilder
                    .baseUrl(order.getStore().getCallbackUrl())
                    .defaultHeader("Content-Type", "application/json")
                    .build()
                    .post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .toEntity(String.class)
                    .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                            .maxBackoff(Duration.ofSeconds(10))
                            .doBeforeRetry(r ->
                                log.warn("{} Retry {} for sending order with id {}: {}", LOG_PREFIX, r.totalRetries() + 1, order.getId(), r.failure().getMessage())
                            )
                    )
                    .block();
                if (response == null || response.getBody() == null) throw new RuntimeException("Response for order sending request is null");
                String eventId = objectMapper.readTree(response.getBody()).get("eventId").asText();
                log.info("{} Finished sending order with id {} as an event to be registered — event identifier: {}", LOG_PREFIX, order.getId(), eventId);
            } catch (WebClientResponseException e) {
                throw new RuntimeException("Response for order sending request indicates a failed operation: [" + e.getStatusCode() + "] " + e.getResponseBodyAsString());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse JSON response", e);
            }
        } catch (Exception e) {
            log.error("{} Failed sending order with id {} as an event to be registered", LOG_PREFIX, order.getId(), e);
            throw e;
        }
    }

}
