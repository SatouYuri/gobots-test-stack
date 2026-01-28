package dev.quatern.marketplace.controller;

import dev.quatern.marketplace.dto.OrderCreateRequestDTO;
import dev.quatern.marketplace.dto.OrderCreateRequestDTOMapper;
import dev.quatern.marketplace.dto.OrderCreateResponseDTO;
import dev.quatern.marketplace.dto.OrderCreateResponseDTOMapper;
import dev.quatern.marketplace.dto.OrderGetResponseDTO;
import dev.quatern.marketplace.dto.OrderGetResponseDTOMapper;
import dev.quatern.marketplace.dto.OrderUpdateStatusRequestDTO;
import dev.quatern.marketplace.dto.OrderUpdateStatusResponseDTO;
import dev.quatern.marketplace.dto.OrderUpdateStatusResponseDTOMapper;
import dev.quatern.marketplace.model.Order;
import dev.quatern.marketplace.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderCreateRequestDTOMapper orderCreateRequestDTOMapper;
    private final OrderCreateResponseDTOMapper orderCreateResponseDTOMapper;
    private final OrderGetResponseDTOMapper orderGetResponseDTOMapper;
    private final OrderUpdateStatusResponseDTOMapper orderUpdateStatusResponseDTOMapper;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderCreateResponseDTO> create(@RequestBody @Valid OrderCreateRequestDTO requestDTO) {
        Order order = orderService.create(orderCreateRequestDTOMapper.toEntity(requestDTO.order()), requestDTO.storeId());
        return ResponseEntity.ok().body(
            new OrderCreateResponseDTO(
                "Order successfully created",
                orderCreateResponseDTOMapper.toDTO(order)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderGetResponseDTO> get(@PathVariable String id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok().body(
            new OrderGetResponseDTO(
                "Order successfully fetched",
                orderGetResponseDTOMapper.toDTO(order)
            )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderUpdateStatusResponseDTO> updateStatus(
        @PathVariable String id,
        @RequestBody @Valid OrderUpdateStatusRequestDTO requestDTO
    ) {
        Order order = orderService.updateStatus(id, requestDTO.status());
        return ResponseEntity.ok().body(
            new OrderUpdateStatusResponseDTO(
                "Order status successfully updated",
                orderUpdateStatusResponseDTOMapper.toDTO(order)
            )
        );
    }

}
