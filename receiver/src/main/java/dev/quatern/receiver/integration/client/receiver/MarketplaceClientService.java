package dev.quatern.receiver.integration.client.receiver;

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

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketplaceClientService {

    @Value("${marketplace.api.url}")
    private String marketplaceApiUrl;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public void pullUpdateSubjectAsEventSnapshot(Event event) { //TODO: Adicionar logs nessa classe toda...
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
                    .block();
                if (response == null || response.getBody() == null) throw new RuntimeException("Resposta da requisição de envio/atualização de cliente é nula"); //TODO: Ajustar essa mensagem
                event.setSubjectSnapshot(
                    objectMapper.writeValueAsString(
                        (objectMapper.readTree(response.getBody())).get("order")
                    )
                );
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
