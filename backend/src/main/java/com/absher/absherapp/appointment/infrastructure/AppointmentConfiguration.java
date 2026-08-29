package com.absher.absherapp.appointment.infrastructure;

import com.absher.absherapp.appointment.application.*;
import com.absher.absherapp.appointment.application.port.out.*;
import com.absher.absherapp.request.application.PassportRequestActorAccess; import com.absher.absherapp.notification.application.NotificationService;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import java.time.Clock;
import org.springframework.context.annotation.*;

@Configuration
public class AppointmentConfiguration {
    @Bean AppointmentSlotService appointmentSlotService(PassportRequestActorAccess a, AppointmentSlotStore s, OperationalAuditStore audit, Clock c) { return new AppointmentSlotService(a,s,audit,c); }
    @Bean AppointmentService appointmentService(PassportRequestActorAccess a, AppointmentSlotStore s, AppointmentStore p, OperationalAuditStore audit, NotificationService n, Clock c) { return new AppointmentService(a,s,p,audit,n,c); }
}
