package dev.quatern.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.quatern.marketplace.dto.OrderCreateRequestDTO;
import dev.quatern.marketplace.dto.OrderCreateResponseDTO;
import dev.quatern.marketplace.dto.OrderGetResponseDTO;
import dev.quatern.marketplace.dto.OrderUpdateStatusRequestDTO;
import dev.quatern.marketplace.dto.OrderUpdateStatusResponseDTO;
import dev.quatern.marketplace.model.Order;
import dev.quatern.marketplace.dto.OrderCreateRequestDTOMapper;
import dev.quatern.marketplace.dto.OrderCreateResponseDTOMapper;
import dev.quatern.marketplace.dto.OrderGetResponseDTOMapper;
import dev.quatern.marketplace.dto.OrderUpdateStatusResponseDTOMapper;
import dev.quatern.marketplace.service.OrderService;
import dev.quatern.marketplace.enums.OrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private OrderCreateRequestDTOMapper orderCreateRequestDTOMapper;

    @Mock
    private OrderCreateResponseDTOMapper orderCreateResponseDTOMapper;

    @Mock
    private OrderGetResponseDTOMapper orderGetResponseDTOMapper;

    @Mock
    private OrderUpdateStatusResponseDTOMapper orderUpdateStatusResponseDTOMapper;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order sampleOrder;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .setValidator(validator)
            .build();

        sampleOrder = new Order();
        sampleOrder.setId("order-id-123");
        sampleOrder.setStatus(OrderStatusEnum.CREATED);
        sampleOrder.setCreatedAt(LocalDateTime.of(2023,1,1,0,0));
        sampleOrder.setUpdatedAt(LocalDateTime.of(2023,1,1,0,0));
    }

    @Test
    void create_shouldReturnCreatedResponse() throws Exception {
        OrderCreateRequestDTO.Order orderDto = new OrderCreateRequestDTO.Order("mock");
        OrderCreateRequestDTO requestDTO = new OrderCreateRequestDTO("store-1", orderDto);

        when(orderCreateRequestDTOMapper.toEntity(ArgumentMatchers.any())).thenReturn(sampleOrder);
        when(orderService.create(any(Order.class), eq("store-1"))).thenReturn(sampleOrder);
        // prepare response dto
        OrderCreateResponseDTO.Order responseOrder = new OrderCreateResponseDTO.Order(
            sampleOrder.getId(),
            "CREATED",
            sampleOrder.getCreatedAt(),
            sampleOrder.getUpdatedAt()
        );
        when(orderCreateResponseDTOMapper.toDTO(sampleOrder)).thenReturn(responseOrder);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", is("Order successfully created")))
            .andExpect(jsonPath("$.order.id", is(sampleOrder.getId())));

        verify(orderCreateRequestDTOMapper, times(1)).toEntity(ArgumentMatchers.any());
        verify(orderService, times(1)).create(any(Order.class), eq("store-1"));
        verify(orderCreateResponseDTOMapper, times(1)).toDTO(sampleOrder);
    }

    @Test
    void get_shouldReturnFetchedResponse() throws Exception {
        when(orderService.findById("order-id-123")).thenReturn(sampleOrder);
        OrderGetResponseDTO.Order responseOrder = new OrderGetResponseDTO.Order(
            sampleOrder.getId(),
            "CREATED",
            sampleOrder.getCreatedAt(),
            sampleOrder.getUpdatedAt()
        );
        when(orderGetResponseDTOMapper.toDTO(sampleOrder)).thenReturn(responseOrder);

        mockMvc.perform(get("/orders/order-id-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", is("Order successfully fetched")))
            .andExpect(jsonPath("$.order.id", is(sampleOrder.getId())));

        verify(orderService, times(1)).findById("order-id-123");
        verify(orderGetResponseDTOMapper, times(1)).toDTO(sampleOrder);
    }

    @Test
    void updateStatus_shouldReturnUpdatedResponse() throws Exception {
        // use a valid transition: CREATED -> PAID
        OrderUpdateStatusRequestDTO requestDTO = new OrderUpdateStatusRequestDTO(OrderStatusEnum.PAID);
        // set expected persisted order status to PAID
        sampleOrder.setStatus(OrderStatusEnum.PAID);
        when(orderService.updateStatus(eq("order-id-123"), eq(OrderStatusEnum.PAID))).thenReturn(sampleOrder);
        OrderUpdateStatusResponseDTO.Order responseOrder = new OrderUpdateStatusResponseDTO.Order(
            sampleOrder.getId(),
            "PAID",
            sampleOrder.getCreatedAt(),
            sampleOrder.getUpdatedAt()
        );
        when(orderUpdateStatusResponseDTOMapper.toDTO(sampleOrder)).thenReturn(responseOrder);

        mockMvc.perform(patch("/orders/order-id-123/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.message", is("Order status successfully updated")))
              .andExpect(jsonPath("$.order.id", is(sampleOrder.getId())));

         verify(orderService, times(1)).updateStatus(eq("order-id-123"), eq(OrderStatusEnum.PAID));
         verify(orderUpdateStatusResponseDTOMapper, times(1)).toDTO(sampleOrder);
     }

}
