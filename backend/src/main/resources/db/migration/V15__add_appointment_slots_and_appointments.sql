CREATE TABLE appointment_slots (
    id CHAR(36) PRIMARY KEY,
    service_type VARCHAR(32) NOT NULL,
    office_name VARCHAR(160) NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    capacity INT NOT NULL,
    reserved_count INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by CHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_appointment_slots_creator FOREIGN KEY (created_by) REFERENCES accounts(id),
    CONSTRAINT chk_appointment_slot_time CHECK (ends_at > starts_at),
    CONSTRAINT chk_appointment_slot_capacity CHECK (capacity > 0),
    CONSTRAINT chk_appointment_slot_reserved CHECK (reserved_count >= 0 AND reserved_count <= capacity)
);

CREATE INDEX idx_appointment_slots_available ON appointment_slots(service_type, active, starts_at);

CREATE TABLE appointments (
    id CHAR(36) PRIMARY KEY,
    citizen_id CHAR(36) NOT NULL,
    appointment_slot_id CHAR(36) NOT NULL,
    service_type VARCHAR(32) NOT NULL,
    office_name VARCHAR(160) NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL,
    booked_at TIMESTAMP NOT NULL,
    cancelled_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    completed_by CHAR(36) NULL,
    active_booking_service VARCHAR(32) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_appointments_citizen FOREIGN KEY (citizen_id) REFERENCES citizens(id),
    CONSTRAINT fk_appointments_slot FOREIGN KEY (appointment_slot_id) REFERENCES appointment_slots(id),
    CONSTRAINT fk_appointments_completed_by FOREIGN KEY (completed_by) REFERENCES accounts(id)
);

CREATE UNIQUE INDEX uq_appointments_active_citizen_service ON appointments(citizen_id, active_booking_service);
CREATE INDEX idx_appointments_citizen_booked ON appointments(citizen_id, booked_at);
CREATE INDEX idx_appointments_status_starts ON appointments(status, starts_at);
