package dev.quatern.marketplace.service;

import dev.quatern.marketplace.enums.OrderStatusEnum;
import dev.quatern.marketplace.integration.client.receiver.ReceiverClientService;
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
    private final ReceiverClientService receiverClientService;

    public Order create(Order order, String storeId) {
        Store store = storeService.findById(storeId);
        order.setStore(store);
        Order persistedOrder = orderRepository.save(order);
        receiverClientService.sendOrder(persistedOrder); //TODO: Cercar isso com try-catch e tratamento com job depois em caso de falha...
        return persistedOrder;
    }

    public Order updateStatus(String id, OrderStatusEnum status) {
        Order order = findById(id);
        //TODO: Adicionar aqui validações de máquina de estado para o status do pedido
        order.setStatus(status);
        Order persistedOrder = orderRepository.save(order);
        receiverClientService.sendOrder(persistedOrder); //TODO: Cercar isso com try-catch e tratamento com job depois em caso de falha...
        return persistedOrder;
    }

}
