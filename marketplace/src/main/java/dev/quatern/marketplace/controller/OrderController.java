package dev.quatern.marketplace.controller;

import dev.quatern.marketplace.dto.OrderCreateRequestDTO;
import dev.quatern.marketplace.dto.OrderCreateRequestDTOMapper;
import dev.quatern.marketplace.dto.OrderCreateResponseDTO;
import dev.quatern.marketplace.dto.OrderCreateResponseDTOMapper;
import dev.quatern.marketplace.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderCreateResponseDTO> create(@RequestBody @Valid OrderCreateRequestDTO requestDTO) {
        return ResponseEntity.ok().body(
            new OrderCreateResponseDTO(
                "Order successfully created",
                orderCreateResponseDTOMapper.toDTO(orderService.create(orderCreateRequestDTOMapper.toEntity(requestDTO)))
            )
        );
    }

}
