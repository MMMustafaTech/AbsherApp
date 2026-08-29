package com.absher.absherapp.appointment.application.port.out;

import com.absher.absherapp.appointment.domain.*;
import java.time.Instant;
import java.util.*;
public interface AppointmentSlotStore { Optional<AppointmentSlot> findByIdForUpdate(UUID id); AppointmentSlot save(AppointmentSlot slot); List<AppointmentSlot> findAvailable(AppointmentServiceType type, Instant now); }
