package com.absher.absherapp.appointment.infrastructure.web;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.appointment.application.*;
import com.absher.absherapp.appointment.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AppointmentController {
    private final AppointmentSlotService slotService; private final AppointmentService appointmentService;
    public AppointmentController(AppointmentSlotService slotService, AppointmentService appointmentService) { this.slotService=slotService; this.appointmentService=appointmentService; }
    @GetMapping("/me/appointment-slots") public List<AppointmentSlot> availableSlots(@AuthenticationPrincipal Jwt jwt,@RequestParam AppointmentServiceType serviceType) { return slotService.available(accountId(jwt),serviceType); }
    @PostMapping("/me/appointments") @ResponseStatus(HttpStatus.CREATED) public Appointment book(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody Booking body) { return appointmentService.book(accountId(jwt),body.slotId()); }
    @GetMapping("/me/appointments") public List<Appointment> mine(@AuthenticationPrincipal Jwt jwt) { return appointmentService.mine(accountId(jwt)); }
    @PostMapping("/me/appointments/{appointmentId}/cancel") public Appointment cancel(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID appointmentId) { return appointmentService.cancelMine(accountId(jwt),appointmentId); }
    @PostMapping("/operations/appointment-slots") @ResponseStatus(HttpStatus.CREATED) public AppointmentSlot createSlot(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody SlotSubmission body) { return slotService.create(accountId(jwt),body.serviceType(),body.officeName(),body.startsAt(),body.endsAt(),body.capacity()); }
    @GetMapping("/operations/appointments") public List<Appointment> operationalAppointments(@AuthenticationPrincipal Jwt jwt,@RequestParam(defaultValue="BOOKED") AppointmentStatus status) { return appointmentService.byStatus(accountId(jwt),status); }
    @PostMapping("/operations/appointments/{appointmentId}/complete") public Appointment complete(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID appointmentId) { return appointmentService.complete(accountId(jwt),appointmentId); }
    private AccountId accountId(Jwt jwt){return new AccountId(UUID.fromString(jwt.getSubject()));}
    public record Booking(@NotNull UUID slotId) { }
    public record SlotSubmission(@NotNull AppointmentServiceType serviceType,@NotBlank @Size(max=160) String officeName,@NotNull Instant startsAt,@NotNull Instant endsAt,@Min(1) @Max(500) int capacity) { }
}
