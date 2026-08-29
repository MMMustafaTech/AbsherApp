package com.absher.absherapp.appointment.domain;

import com.absher.absherapp.account.domain.AccountId;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record AppointmentSlot(UUID id, AppointmentServiceType serviceType, String officeName, Instant startsAt, Instant endsAt,
                              int capacity, int reservedCount, boolean active, AccountId createdBy, Instant createdAt) {
    public AppointmentSlot {
        Objects.requireNonNull(id); Objects.requireNonNull(serviceType); officeName = requiredOffice(officeName); Objects.requireNonNull(startsAt); Objects.requireNonNull(endsAt); Objects.requireNonNull(createdBy); Objects.requireNonNull(createdAt);
        if (!endsAt.isAfter(startsAt)) throw new IllegalArgumentException("Appointment slot end time must be after its start time");
        if (capacity < 1 || capacity > 500) throw new IllegalArgumentException("Appointment capacity must be between 1 and 500");
        if (reservedCount < 0 || reservedCount > capacity) throw new IllegalArgumentException("Invalid appointment slot reservation count");
    }
    public static AppointmentSlot create(AppointmentServiceType serviceType, String officeName, Instant startsAt, Instant endsAt, int capacity, AccountId createdBy, Instant now) { return new AppointmentSlot(UUID.randomUUID(), serviceType, officeName, startsAt, endsAt, capacity, 0, true, createdBy, now); }
    public AppointmentSlot reserve(Instant now) { if (!active || !startsAt.isAfter(now)) throw new IllegalStateException("Appointment slot is no longer available"); if (reservedCount >= capacity) throw new IllegalStateException("Appointment slot is full"); return new AppointmentSlot(id, serviceType, officeName, startsAt, endsAt, capacity, reservedCount + 1, true, createdBy, createdAt); }
    public AppointmentSlot release() { if (reservedCount < 1) throw new IllegalStateException("Appointment slot has no reservation to release"); return new AppointmentSlot(id, serviceType, officeName, startsAt, endsAt, capacity, reservedCount - 1, active, createdBy, createdAt); }
    public boolean availableAt(Instant now) { return active && startsAt.isAfter(now) && reservedCount < capacity; }
    private static String requiredOffice(String value) { String normalized = value == null ? null : value.trim(); if (normalized == null || normalized.isEmpty() || normalized.length() > 160) throw new IllegalArgumentException("Office name is required and must not exceed 160 characters"); return normalized; }
}
