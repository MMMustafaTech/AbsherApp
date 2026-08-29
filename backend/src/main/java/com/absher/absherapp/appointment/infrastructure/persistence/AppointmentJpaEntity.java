package com.absher.absherapp.appointment.infrastructure.persistence;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.appointment.domain.*;
import com.absher.absherapp.citizen.domain.CitizenId;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="appointments")
class AppointmentJpaEntity {
    @Id @Column(length=36,columnDefinition="CHAR(36)") private String id;
    @Column(name="citizen_id",nullable=false,length=36,columnDefinition="CHAR(36)") private String citizenId;
    @Column(name="appointment_slot_id",nullable=false,length=36,columnDefinition="CHAR(36)") private String slotId;
    @Enumerated(EnumType.STRING) @Column(name="service_type",nullable=false,length=32) private AppointmentServiceType serviceType;
    @Column(name="office_name",nullable=false,length=160) private String officeName;
    @Column(name="starts_at",nullable=false) private Instant startsAt;
    @Column(name="ends_at",nullable=false) private Instant endsAt;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=32) private AppointmentStatus status;
    @Column(name="booked_at",nullable=false) private Instant bookedAt;
    @Column(name="cancelled_at") private Instant cancelledAt;
    @Column(name="completed_at") private Instant completedAt;
    @Column(name="completed_by",length=36,columnDefinition="CHAR(36)") private String completedBy;
    @Column(name="active_booking_service",length=32) private String activeBookingService;
    @Version private Long version;
    protected AppointmentJpaEntity() { }
    private AppointmentJpaEntity(Appointment appointment) { apply(appointment); }
    static AppointmentJpaEntity from(Appointment appointment){return new AppointmentJpaEntity(appointment);}
    void apply(Appointment a){id=a.id().toString(); citizenId=a.citizenId().value().toString(); slotId=a.slotId().toString(); serviceType=a.serviceType(); officeName=a.officeName(); startsAt=a.startsAt(); endsAt=a.endsAt(); status=a.status(); bookedAt=a.bookedAt(); cancelledAt=a.cancelledAt(); completedAt=a.completedAt(); completedBy=a.completedBy()==null?null:a.completedBy().value().toString(); activeBookingService=a.status().isActive()?a.serviceType().name():null;}
    Appointment toDomain(){return new Appointment(UUID.fromString(id),new CitizenId(UUID.fromString(citizenId)),UUID.fromString(slotId),serviceType,officeName,startsAt,endsAt,status,bookedAt,cancelledAt,completedAt,completedBy==null?null:new AccountId(UUID.fromString(completedBy)));}
}
