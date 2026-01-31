package dev.quatern.receiver.integration.client.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.quatern.receiver.enums.EventSubjectTypeEnum;
import dev.quatern.receiver.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketplaceClientService {

    @Value("${marketplace.api.url}")
    private String marketplaceApiUrl;

    private static final String LOG_PREFIX = "[Marketplace]";

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public void fetchSubjectAsEventSnapshot(Event event) {
        log.info("{} Fetching subject with marketplaceSubjectId {} from event with id {} as a JSON snapshot", LOG_PREFIX, event.getMarketplaceSubjectId(), event.getId());
        try {
            String subjectSnapshotUrl = marketplaceApiUrl;
            switch (event.getSubjectType()) {
                case EventSubjectTypeEnum.ORDER -> subjectSnapshotUrl += "/orders/" + event.getMarketplaceSubjectId();
                // Aqui seriam declarados tratamentos para os outros domínios que precisam ter atualizações (sobre suas entidades) registradas no Receiver
            }
            try {
                ResponseEntity<String> response = webClientBuilder
                    .baseUrl(subjectSnapshotUrl)
                    .defaultHeader("Content-Type", "application/json")
                    .build()
                    .get()
                    .retrieve()
                    .toEntity(String.class)
                    .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                            .maxBackoff(Duration.ofSeconds(10))
                            .doBeforeRetry(r ->
                                log.warn("{} Retry {} for fetching subject with marketplaceSubjectId {} from event with id {}: {}", LOG_PREFIX, r.totalRetries() + 1, event.getMarketplaceSubjectId(), event.getId(), r.failure().getMessage())
                            )
                    )
                    .block();
                if (response == null || response.getBody() == null) throw new RuntimeException("Response for subject fetching request is null");
                event.setSubjectSnapshot(
                    objectMapper.writeValueAsString(objectMapper.readTree(response.getBody()).get("order"))
                );
                log.info("{} Finished fetching subject with marketplaceSubjectId {} from event with id {} as a JSON snapshot", LOG_PREFIX, event.getMarketplaceSubjectId(), event.getId());
            } catch (WebClientResponseException e) {
                throw new RuntimeException("Response for subject fetching request indicates a failed operation: [" + e.getStatusCode() + "] " + e.getResponseBodyAsString());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse JSON response", e);
            }
        } catch (Exception e) {
            log.error("{} Failed fetching subject with marketplaceSubjectId {} from event with id {} as a JSON snapshot", LOG_PREFIX, event.getMarketplaceSubjectId(), event.getId(), e);
            throw e;
        }
    }

}
