package dev.quatern.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.quatern.marketplace.dto.StoreCreateRequestDTO;
import dev.quatern.marketplace.dto.StoreCreateResponseDTO;
import dev.quatern.marketplace.dto.StoreGetResponseDTO;
import dev.quatern.marketplace.dto.StoreUpdateRequestDTO;
import dev.quatern.marketplace.dto.StoreUpdateResponseDTO;
import dev.quatern.marketplace.model.Store;
import dev.quatern.marketplace.dto.StoreCreateRequestDTOMapper;
import dev.quatern.marketplace.dto.StoreCreateResponseDTOMapper;
import dev.quatern.marketplace.dto.StoreGetResponseDTOMapper;
import dev.quatern.marketplace.dto.StoreUpdateRequestDTOMapper;
import dev.quatern.marketplace.dto.StoreUpdateResponseDTOMapper;
import dev.quatern.marketplace.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class StoreControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private StoreCreateRequestDTOMapper storeCreateRequestDTOMapper;

    @Mock
    private StoreCreateResponseDTOMapper storeCreateResponseDTOMapper;

    @Mock
    private StoreGetResponseDTOMapper storeGetResponseDTOMapper;

    @Mock
    private StoreUpdateRequestDTOMapper storeUpdateRequestDTOMapper;

    @Mock
    private StoreUpdateResponseDTOMapper storeUpdateResponseDTOMapper;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StoreController storeController;

    private Store sampleStore;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(storeController)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .setValidator(validator)
            .build();

        sampleStore = new Store();
        sampleStore.setId("store-id-1");
        sampleStore.setName("My Store");
        sampleStore.setCallbackUrl("https://example.com/cb");
        sampleStore.setCreatedAt(LocalDateTime.of(2023,1,1,0,0));
        sampleStore.setUpdatedAt(LocalDateTime.of(2023,1,1,0,0));
    }

    @Test
    void create_shouldReturnCreatedResponse() throws Exception {
        StoreCreateRequestDTO.Store dto = new StoreCreateRequestDTO.Store("My Store", "https://example.com/cb");
        StoreCreateRequestDTO requestDTO = new StoreCreateRequestDTO(dto);

        when(storeCreateRequestDTOMapper.toEntity(any())).thenReturn(sampleStore);
        when(storeService.create(any(Store.class))).thenReturn(sampleStore);
        StoreCreateResponseDTO.Store responseStore = new StoreCreateResponseDTO.Store(
            sampleStore.getId(), sampleStore.getName(), sampleStore.getCallbackUrl(), sampleStore.getCreatedAt(), sampleStore.getUpdatedAt()
        );
        when(storeCreateResponseDTOMapper.toDTO(sampleStore)).thenReturn(responseStore);

        mockMvc.perform(post("/stores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", is("Store successfully created")))
            .andExpect(jsonPath("$.store.id", is(sampleStore.getId())))
            .andExpect(jsonPath("$.store.name", is(sampleStore.getName())));

        verify(storeCreateRequestDTOMapper, times(1)).toEntity(any());
        verify(storeService, times(1)).create(any(Store.class));
        verify(storeCreateResponseDTOMapper, times(1)).toDTO(sampleStore);
    }

    @Test
    void get_shouldReturnFetchedResponse() throws Exception {
        when(storeService.findById("store-id-1")).thenReturn(sampleStore);
        StoreGetResponseDTO.Store responseStore = new StoreGetResponseDTO.Store(
            sampleStore.getId(), sampleStore.getName(), sampleStore.getCallbackUrl(), sampleStore.getCreatedAt(), sampleStore.getUpdatedAt()
        );
        when(storeGetResponseDTOMapper.toDTO(sampleStore)).thenReturn(responseStore);

        mockMvc.perform(get("/stores/store-id-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", is("Store successfully fetched")))
            .andExpect(jsonPath("$.store.id", is(sampleStore.getId())))
            .andExpect(jsonPath("$.store.name", is(sampleStore.getName())));

        verify(storeService, times(1)).findById("store-id-1");
        verify(storeGetResponseDTOMapper, times(1)).toDTO(sampleStore);
    }

    @Test
    void update_shouldReturnUpdatedResponse() throws Exception {
        StoreUpdateRequestDTO.Store dto = new StoreUpdateRequestDTO.Store("My Store Updated", "https://example.com/cb2");
        StoreUpdateRequestDTO requestDTO = new StoreUpdateRequestDTO(dto);

        when(storeService.findById("store-id-1")).thenReturn(sampleStore);
        // storeUpdateRequestDTOMapper.map should modify the sampleStore in-place; we'll simulate by leaving sampleStore modified
        doAnswer(invocation -> {
            StoreUpdateRequestDTO.Store src = invocation.getArgument(0);
            Store target = invocation.getArgument(1);
            target.setName(src.name());
            target.setCallbackUrl(src.callbackUrl());
            return null;
        }).when(storeUpdateRequestDTOMapper).map(any(), any());

        when(storeService.save(any(Store.class))).thenReturn(sampleStore);

        StoreUpdateResponseDTO.Store responseStore = new StoreUpdateResponseDTO.Store(
            sampleStore.getId(), "My Store Updated", "https://example.com/cb2", sampleStore.getCreatedAt(), sampleStore.getUpdatedAt()
        );
        when(storeUpdateResponseDTOMapper.toDTO(sampleStore)).thenReturn(responseStore);

        mockMvc.perform(patch("/stores/store-id-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", is("Store successfully updated")))
            .andExpect(jsonPath("$.store.id", is(sampleStore.getId())))
            .andExpect(jsonPath("$.store.name", is("My Store Updated")));

        verify(storeService, times(1)).findById("store-id-1");
        verify(storeUpdateRequestDTOMapper, times(1)).map(any(), any());
        verify(storeService, times(1)).save(any(Store.class));
        verify(storeUpdateResponseDTOMapper, times(1)).toDTO(sampleStore);
    }

}
