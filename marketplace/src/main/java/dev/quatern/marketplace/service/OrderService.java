package dev.quatern.marketplace.service;

import dev.quatern.marketplace.enums.OrderStatusEnum;
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

    public Order create(Order order, String storeId) {
        Store store = storeService.findById(storeId);
        order.setStore(store);
        Order persistedOrder = orderRepository.save(order);
        //TODO: Enviar aqui evento de criação de pedido para o callbackUrl da loja ligada ao pedido
        return persistedOrder;
    }

    public Order updateStatus(String id, OrderStatusEnum status) {
        Order order = findById(id);
        //TODO: Adicionar aqui validações de máquina de estado para o status do pedido
        order.setStatus(status);
        Order persistedOrder = orderRepository.save(order);
        //TODO: Enviar aqui evento de atualização de status para o callbackUrl da loja ligada ao pedido
        return persistedOrder;
    }

}
