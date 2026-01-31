package dev.quatern.receiver.model;

import dev.quatern.receiver.enums.EventSubjectTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    private String id = UUID.randomUUID().toString();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime receivedAt;

    private String type;

    @Enumerated(EnumType.STRING)
    private EventSubjectTypeEnum subjectType;

    private String marketplaceStoreId;

    private String marketplaceSubjectId;

    private String subjectSnapshot;

}
