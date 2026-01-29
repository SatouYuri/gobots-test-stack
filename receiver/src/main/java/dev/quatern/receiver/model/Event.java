package dev.quatern.receiver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
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

    private String marketplaceStoreId;

    private String marketplaceSubjectId;

    private String subjectSnapshot;

}
