package com.absher.absherapp.appointment.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.appointment.application.port.out.*;
import com.absher.absherapp.appointment.domain.*;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import com.absher.absherapp.notification.application.NotificationService;
import com.absher.absherapp.notification.domain.NotificationType;
import java.time.*;
import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

public class AppointmentService {
    private final PassportRequestActorAccess access; private final AppointmentSlotStore slots; private final AppointmentStore appointments; private final OperationalAuditStore audit; private final NotificationService notifications; private final Clock clock;
    public AppointmentService(PassportRequestActorAccess access, AppointmentSlotStore slots, AppointmentStore appointments, OperationalAuditStore audit, NotificationService notifications, Clock clock) { this.access=access; this.slots=slots; this.appointments=appointments; this.audit=audit; this.notifications=notifications; this.clock=clock; }
    @Transactional public Appointment book(AccountId actor, UUID slotId) { CitizenId citizen=access.requireActiveCitizen(actor); AppointmentSlot slot=slots.findByIdForUpdate(slotId).orElseThrow(()->new NotFoundException("Appointment slot not found")); if (appointments.hasActiveAppointment(citizen, slot.serviceType())) throw new AppointmentConflictException("An active appointment already exists for this service"); Instant now=Instant.now(clock); try { AppointmentSlot reservedSlot=slot.reserve(now); Appointment appointment=Appointment.book(citizen, reservedSlot, now); appointments.save(appointment); slots.save(reservedSlot); audit.record(actor.value().toString(),"APPOINTMENT_BOOKED","APPOINTMENT",appointment.id().toString(),"slotId="+slotId,now); notifications.publish(citizen, NotificationType.APPOINTMENT_BOOKED, "Appointment booked", "Your appointment has been booked successfully."); return appointment; } catch (DataIntegrityViolationException exception) { throw new AppointmentConflictException("An active appointment already exists for this service"); } catch (IllegalStateException exception) { throw new AppointmentConflictException(exception.getMessage()); } }
    @Transactional public Appointment cancelMine(AccountId actor, UUID appointmentId) { CitizenId citizen=access.requireActiveCitizen(actor); Appointment current=owned(citizen, appointmentId); Instant now=Instant.now(clock); try { Appointment updated=current.cancel(now); AppointmentSlot slot=slots.findByIdForUpdate(current.slotId()).orElseThrow(()->new NotFoundException("Appointment slot not found")); slots.save(slot.release()); appointments.save(updated); audit.record(actor.value().toString(),"APPOINTMENT_CANCELLED","APPOINTMENT",appointmentId.toString(),null,now); notifications.publish(citizen, NotificationType.APPOINTMENT_CANCELLED, "Appointment cancelled", "Your appointment has been cancelled."); return updated; } catch (IllegalStateException exception) { throw new AppointmentConflictException(exception.getMessage()); } }
    @Transactional public Appointment complete(AccountId actor, UUID appointmentId) { access.requireActiveOperator(actor); Appointment current=appointments.findByIdForUpdate(appointmentId).orElseThrow(()->new NotFoundException("Appointment not found")); Instant now=Instant.now(clock); try { Appointment updated=current.complete(actor,now); appointments.save(updated); audit.record(actor.value().toString(),"APPOINTMENT_COMPLETED","APPOINTMENT",appointmentId.toString(),null,now); notifications.publish(updated.citizenId(), NotificationType.APPOINTMENT_COMPLETED, "Appointment completed", "Your appointment has been marked as completed."); return updated; } catch (IllegalStateException exception) { throw new AppointmentConflictException(exception.getMessage()); } }
    public List<Appointment> mine(AccountId actor) { return appointments.findByCitizenId(access.requireActiveCitizen(actor)); }
    public List<Appointment> byStatus(AccountId actor, AppointmentStatus status) { access.requireActiveOperator(actor); return appointments.findByStatus(status); }
    private Appointment owned(CitizenId citizen, UUID id) { Appointment appointment=appointments.findByIdForUpdate(id).orElseThrow(()->new NotFoundException("Appointment not found")); if (!appointment.belongsTo(citizen)) throw new NotFoundException("Appointment not found"); return appointment; }
}
