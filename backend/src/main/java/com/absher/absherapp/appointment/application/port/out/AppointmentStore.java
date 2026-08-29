package com.absher.absherapp.appointment.application.port.out;

import com.absher.absherapp.appointment.domain.*;
import com.absher.absherapp.citizen.domain.CitizenId;
import java.util.*;
public interface AppointmentStore { boolean hasActiveAppointment(CitizenId citizenId, AppointmentServiceType serviceType); Optional<Appointment> findByIdForUpdate(UUID id); Appointment save(Appointment appointment); List<Appointment> findByCitizenId(CitizenId citizenId); List<Appointment> findByStatus(AppointmentStatus status); }
