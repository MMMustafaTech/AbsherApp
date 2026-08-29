package com.absher.absherapp.appointment.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.appointment.application.port.out.AppointmentSlotStore;
import com.absher.absherapp.appointment.domain.*;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import java.time.*;
import java.util.List;

public class AppointmentSlotService {
    private final PassportRequestActorAccess access; private final AppointmentSlotStore slots; private final OperationalAuditStore audit; private final Clock clock;
    public AppointmentSlotService(PassportRequestActorAccess access, AppointmentSlotStore slots, OperationalAuditStore audit, Clock clock) { this.access=access; this.slots=slots; this.audit=audit; this.clock=clock; }
    public AppointmentSlot create(AccountId actor, AppointmentServiceType type, String office, Instant startsAt, Instant endsAt, int capacity) { access.requireActiveOperator(actor); Instant now=Instant.now(clock); if (!startsAt.isAfter(now)) throw new IllegalArgumentException("Appointment slot must start in the future"); AppointmentSlot slot=slots.save(AppointmentSlot.create(type, office, startsAt, endsAt, capacity, actor, now)); audit.record(actor.value().toString(), "APPOINTMENT_SLOT_CREATED", "APPOINTMENT_SLOT", slot.id().toString(), "serviceType="+type, now); return slot; }
    public List<AppointmentSlot> available(AccountId actor, AppointmentServiceType type) { access.requireActiveCitizen(actor); return slots.findAvailable(type, Instant.now(clock)); }
}
