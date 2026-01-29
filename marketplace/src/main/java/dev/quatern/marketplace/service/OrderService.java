package dev.quatern.marketplace.service;

import dev.quatern.marketplace.enums.OrderStatusEnum;
import dev.quatern.marketplace.integration.client.receiver.ReceiverClient;
import dev.quatern.marketplace.integration.client.receiver.dto.EventListenRequestDTO;
import dev.quatern.marketplace.model.Order;
import dev.quatern.marketplace.model.Store;
import dev.quatern.marketplace.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService extends BaseService<Order> {

    private final OrderRepository orderRepository;
    private final StoreService storeService;
    private final ReceiverClient receiverClient;

    public Order create(Order order, String storeId) {
        Store store = storeService.findById(storeId);
        order.setStore(store);
        Order persistedOrder = orderRepository.save(order);
        sendOrderEvent(persistedOrder);
        return persistedOrder;
    }

    public Order updateStatus(String id, OrderStatusEnum status) {
        Order order = findById(id);
        //TODO: Adicionar aqui validações de máquina de estado para o status do pedido
        order.setStatus(status);
        Order persistedOrder = orderRepository.save(order);
        sendOrderEvent(persistedOrder);
        return persistedOrder;
    }

    public void sendOrderEvent(Order order) {
        //TODO: Substituir uso do ReceiverClient em Feign por um ReceiverClientService que use org.springframework.web.reactive.function.client.WebClient, podendo chamar a url da loja associada ao pedido (order.getStore().getCallbackUrl())
        receiverClient.sendOrderEvent(new EventListenRequestDTO(
            new EventListenRequestDTO.Event(
                "order." + order.getStatus().name().toLowerCase(),
                order.getStore().getId(),
                order.getId()
            )
        ));
    }

}
