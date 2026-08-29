package com.chari.chariapp.appointment.infrastructure.persistence;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.appointment.domain.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="appointment_slots")
class AppointmentSlotJpaEntity {
    @Id @Column(length=36,columnDefinition="CHAR(36)") private String id;
    @Enumerated(EnumType.STRING) @Column(name="service_type",nullable=false,length=32) private AppointmentServiceType serviceType;
    @Column(name="office_name",nullable=false,length=160) private String officeName;
    @Column(name="starts_at",nullable=false) private Instant startsAt;
    @Column(name="ends_at",nullable=false) private Instant endsAt;
    @Column(nullable=false) private int capacity;
    @Column(name="reserved_count",nullable=false) private int reservedCount;
    @Column(nullable=false) private boolean active;
    @Column(name="created_by",nullable=false,length=36,columnDefinition="CHAR(36)") private String createdBy;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    @Version private Long version;
    protected AppointmentSlotJpaEntity() { }
    private AppointmentSlotJpaEntity(AppointmentSlot slot) { apply(slot); }
    static AppointmentSlotJpaEntity from(AppointmentSlot slot) { return new AppointmentSlotJpaEntity(slot); }
    void apply(AppointmentSlot slot) { id=slot.id().toString(); serviceType=slot.serviceType(); officeName=slot.officeName(); startsAt=slot.startsAt(); endsAt=slot.endsAt(); capacity=slot.capacity(); reservedCount=slot.reservedCount(); active=slot.active(); createdBy=slot.createdBy().value().toString(); createdAt=slot.createdAt(); }
    AppointmentSlot toDomain() { return new AppointmentSlot(UUID.fromString(id),serviceType,officeName,startsAt,endsAt,capacity,reservedCount,active,new AccountId(UUID.fromString(createdBy)),createdAt); }
}
