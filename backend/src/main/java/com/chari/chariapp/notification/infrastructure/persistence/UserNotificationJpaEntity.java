package com.chari.chariapp.notification.infrastructure.persistence;
import com.chari.chariapp.citizen.domain.CitizenId; import com.chari.chariapp.notification.domain.*; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="user_notifications") class UserNotificationJpaEntity {
 @Id @Column(length=36,columnDefinition="CHAR(36)") private String id;
 @Column(name="citizen_id",nullable=false,length=36,columnDefinition="CHAR(36)") private String citizenId;
 @Enumerated(EnumType.STRING) @Column(name="notification_type",nullable=false,length=64) private NotificationType type;
 @Column(nullable=false,length=160) private String title;
 @Column(nullable=false,length=1000) private String message;
 @Column(name="read_at") private Instant readAt;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 @Version private Long version;
 protected UserNotificationJpaEntity() { }
 private UserNotificationJpaEntity(UserNotification n){apply(n);} static UserNotificationJpaEntity from(UserNotification n){return new UserNotificationJpaEntity(n);}
 void apply(UserNotification n){id=n.id().toString();citizenId=n.citizenId().value().toString();type=n.type();title=n.title();message=n.message();readAt=n.readAt();createdAt=n.createdAt();}
 UserNotification toDomain(){return new UserNotification(UUID.fromString(id),new CitizenId(UUID.fromString(citizenId)),type,title,message,readAt,createdAt);}
}
