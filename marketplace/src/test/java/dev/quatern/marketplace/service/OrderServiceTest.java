package dev.quatern.marketplace.service;

import dev.quatern.marketplace.enums.OrderStatusEnum;
import dev.quatern.marketplace.integration.client.receiver.ReceiverClientService;
import dev.quatern.marketplace.model.Order;
import dev.quatern.marketplace.model.Store;
import dev.quatern.marketplace.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private ReceiverClientService receiverClientService;

    @InjectMocks
    private OrderService orderService;

    private Order sampleOrder;
    private Store sampleStore;

    @BeforeEach
    void setup() {
        sampleOrder = new Order();
        sampleOrder.setId("order-1");
        sampleOrder.setStatus(OrderStatusEnum.CREATED);

        sampleStore = new Store();
        sampleStore.setId("store-1");
        sampleStore.setName("Store One");
        sampleStore.setCallbackUrl("https://example.com/cb");
    }

    @Test
    void create_shouldSetStore_saveAndSendOrder() {
        Order toCreate = new Order();
        toCreate.setId("temp");

        when(storeService.findById("store-1")).thenReturn(sampleStore);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.create(toCreate, "store-1");

        assertNotNull(result);
        assertEquals(sampleStore, result.getStore(), "Store must be set on order before save");
        verify(orderRepository, times(1)).save(toCreate);
        verify(receiverClientService, times(1)).sendOrder(result);
    }

    @Test
    void updateStatus_shouldPersistAndSendOnValidTransition() {
        sampleOrder.setStatus(OrderStatusEnum.CREATED);
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order updated = orderService.updateStatus("order-1", OrderStatusEnum.PAID);

        assertNotNull(updated);
        assertEquals(OrderStatusEnum.PAID, updated.getStatus());
        verify(orderRepository, times(1)).save(sampleOrder);
        verify(receiverClientService, times(1)).sendOrder(sampleOrder);
    }

    @Test
    void updateStatus_shouldThrowBadRequestOnInvalidTransition() {
        sampleOrder.setStatus(OrderStatusEnum.CREATED);
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(sampleOrder));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            orderService.updateStatus("order-1", OrderStatusEnum.COMPLETED)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(orderRepository, never()).save(any());
        verify(receiverClientService, never()).sendOrder(any());
    }

}
