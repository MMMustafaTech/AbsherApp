package com.absher.absherapp.home.infrastructure;

import com.absher.absherapp.appointment.application.port.out.AppointmentStore;
import com.absher.absherapp.birthrequest.application.port.out.BirthCertificateRequestStore;
import com.absher.absherapp.home.application.CitizenHomeService;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStore;
import com.absher.absherapp.notification.application.NotificationService;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.request.application.port.out.PassportRequestStore;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration public class HomeConfiguration {
    @Bean CitizenHomeService citizenHomeService(PassportRequestActorAccess access, PassportRequestStore passports,
                                                NationalIdentityRequestStore identities, BirthCertificateRequestStore births,
                                                AppointmentStore appointments, NotificationService notifications, Clock clock) {
        return new CitizenHomeService(access, passports, identities, births, appointments, notifications, clock);
    }
}
