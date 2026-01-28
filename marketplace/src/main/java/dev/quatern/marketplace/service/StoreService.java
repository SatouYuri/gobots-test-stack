package dev.quatern.marketplace.service;

import dev.quatern.marketplace.model.Store;
import dev.quatern.marketplace.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService extends BaseService<Store> {

    private final StoreRepository storeRepository;

    public Store create(Store store) {
        return storeRepository.save(store);
    }

}
