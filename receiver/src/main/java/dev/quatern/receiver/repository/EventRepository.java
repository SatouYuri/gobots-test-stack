package dev.quatern.receiver.repository;

import dev.quatern.receiver.enums.EventSubjectTypeEnum;
import dev.quatern.receiver.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    Optional<Event> findByTypeAndSubjectTypeAndMarketplaceStoreIdAndMarketplaceSubjectId(
        String type,
        EventSubjectTypeEnum subjectType,
        String marketplaceStoreId,
        String marketplaceSubjectId
    );

}
