package dev.quatern.marketplace.service;

import dev.quatern.marketplace.model.Store;
import dev.quatern.marketplace.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    private Store sampleStore;

    @BeforeEach
    void setup() {
        sampleStore = new Store();
        sampleStore.setId("store-1");
        sampleStore.setName("My Store");
        sampleStore.setCallbackUrl("https://example.com/cb");
        sampleStore.setCreatedAt(LocalDateTime.of(2023,1,1,0,0));
        sampleStore.setUpdatedAt(LocalDateTime.of(2023,1,1,0,0));
    }

    @Test
    void create_shouldSaveAndReturnStore() {
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> {
            Store s = invocation.getArgument(0);
            // simulate persistence assigning id (if needed)
            if (s.getId() == null) s.setId("generated-id");
            return s;
        });

        Store toCreate = new Store();
        toCreate.setName("New Store");

        Store result = storeService.create(toCreate);

        assertNotNull(result);
        assertEquals("New Store", result.getName());
        assertNotNull(result.getId());
        verify(storeRepository, times(1)).save(toCreate);
    }

    @Test
    void findById_shouldReturnStoreWhenExists() {
        when(storeRepository.findById("store-1")).thenReturn(Optional.of(sampleStore));

        Store result = storeService.findById("store-1");

        assertNotNull(result);
        assertEquals(sampleStore, result);
        verify(storeRepository, times(1)).findById("store-1");
    }

    @Test
    void findById_shouldThrowNotFoundWhenMissing() {
        when(storeRepository.findById("missing")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> storeService.findById("missing"));
        assertEquals(404, ex.getStatusCode().value());
        verify(storeRepository, times(1)).findById("missing");
    }

    @Test
    void save_shouldCallRepositorySave() {
        when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Store result = storeService.save(sampleStore);

        assertEquals(sampleStore, result);
        verify(storeRepository, times(1)).save(sampleStore);
    }

}
