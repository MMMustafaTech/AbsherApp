package com.chari.chariapp.appointment.domain;

public enum AppointmentStatus {
    BOOKED,
    CANCELLED,
    COMPLETED;

    public boolean isActive() { return this == BOOKED; }
}
