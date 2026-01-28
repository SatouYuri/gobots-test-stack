package dev.quatern.marketplace.controller;

import dev.quatern.marketplace.dto.StoreCreateRequestDTO;
import dev.quatern.marketplace.dto.StoreCreateRequestDTOMapper;
import dev.quatern.marketplace.dto.StoreCreateResponseDTO;
import dev.quatern.marketplace.dto.StoreCreateResponseDTOMapper;
import dev.quatern.marketplace.dto.StoreGetResponseDTO;
import dev.quatern.marketplace.dto.StoreGetResponseDTOMapper;
import dev.quatern.marketplace.dto.StoreUpdateRequestDTO;
import dev.quatern.marketplace.dto.StoreUpdateRequestDTOMapper;
import dev.quatern.marketplace.dto.StoreUpdateResponseDTO;
import dev.quatern.marketplace.dto.StoreUpdateResponseDTOMapper;
import dev.quatern.marketplace.model.Store;
import dev.quatern.marketplace.service.StoreService;
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
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreCreateRequestDTOMapper storeCreateRequestDTOMapper;
    private final StoreCreateResponseDTOMapper storeCreateResponseDTOMapper;
    private final StoreGetResponseDTOMapper storeGetResponseDTOMapper;
    private final StoreUpdateRequestDTOMapper storeUpdateRequestDTOMapper;
    private final StoreUpdateResponseDTOMapper storeUpdateResponseDTOMapper;
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreCreateResponseDTO> create(@RequestBody @Valid StoreCreateRequestDTO requestDTO) {
        Store store = storeService.create(storeCreateRequestDTOMapper.toEntity(requestDTO.store()));
        return ResponseEntity.ok().body(
            new StoreCreateResponseDTO(
                "Store successfully created",
                storeCreateResponseDTOMapper.toDTO(store)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreGetResponseDTO> get(@PathVariable String id) {
        Store store = storeService.findById(id);
        return ResponseEntity.ok().body(
            new StoreGetResponseDTO(
                "Store successfully fetched",
                storeGetResponseDTOMapper.toDTO(store)
            )
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StoreUpdateResponseDTO> update(
        @PathVariable String id,
        @RequestBody @Valid StoreUpdateRequestDTO requestDTO
    ) {
        Store store = storeService.findById(id);
        storeUpdateRequestDTOMapper.map(requestDTO.store(), store);
        storeService.save(store);
        return ResponseEntity.ok().body(
            new StoreUpdateResponseDTO(
                "Store successfully updated",
                storeUpdateResponseDTOMapper.toDTO(store)
            )
        );
    }

}
