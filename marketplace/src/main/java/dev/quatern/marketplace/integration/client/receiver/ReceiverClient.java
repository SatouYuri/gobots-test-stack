package dev.quatern.marketplace.integration.client.receiver;

import dev.quatern.marketplace.integration.client.receiver.dto.EventListenRequestDTO;
import dev.quatern.marketplace.integration.client.receiver.dto.EventListenResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Deprecated //TODO: Substituir uso do ReceiverClient em Feign por um ReceiverClientService que use org.springframework.web.reactive.function.client.WebClient, podendo chamar a url da loja associada ao pedido (order.getStore().getCallbackUrl())
@FeignClient(name = "receiverClient", url = "${receiver.api.url}")
public interface ReceiverClient {

    @PostMapping("/events")
    EventListenResponseDTO sendOrderEvent(@RequestBody EventListenRequestDTO requestDTO);

}
