package com.chari.chariapp.home.infrastructure;

import com.chari.chariapp.appointment.application.port.out.AppointmentStore;
import com.chari.chariapp.birthrequest.application.port.out.BirthCertificateRequestStore;
import com.chari.chariapp.home.application.CitizenHomeService;
import com.chari.chariapp.identityrequest.application.port.out.NationalIdentityRequestStore;
import com.chari.chariapp.notification.application.NotificationService;
import com.chari.chariapp.request.application.PassportRequestActorAccess;
import com.chari.chariapp.request.application.port.out.PassportRequestStore;
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
