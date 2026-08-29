package com.chari.chariapp.citizen.infrastructure;

import com.chari.chariapp.citizen.application.CreateCitizenService;
import com.chari.chariapp.citizen.application.CreateCitizenUseCase;
import com.chari.chariapp.citizen.application.ConfirmCitizenPhoneVerificationService;
import com.chari.chariapp.citizen.application.ConfirmCitizenPhoneVerificationUseCase;
import com.chari.chariapp.citizen.application.RequestCitizenPhoneVerificationService;
import com.chari.chariapp.citizen.application.RequestCitizenPhoneVerificationUseCase;
import com.chari.chariapp.account.application.VerifyEnrollmentOtpUseCase;
import com.chari.chariapp.account.application.port.out.AccountStore;
import com.chari.chariapp.account.application.port.out.EnrollmentChallengeStore;
import com.chari.chariapp.account.application.port.out.OtpCodeGenerator;
import com.chari.chariapp.account.application.port.out.OtpSender;
import com.chari.chariapp.account.application.port.out.VerificationCodeHasher;
import com.chari.chariapp.shared.application.port.out.OperationalAuditStore;
import com.chari.chariapp.citizen.application.port.out.CitizenStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CitizenConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public CreateCitizenUseCase createCitizenUseCase(CitizenStore citizenStore, Clock clock) {
        return new CreateCitizenService(citizenStore, clock);
    }

    @Bean
    public RequestCitizenPhoneVerificationUseCase requestCitizenPhoneVerificationUseCase(
            CitizenStore citizenStore, AccountStore accountStore, EnrollmentChallengeStore challengeStore,
            OtpCodeGenerator codeGenerator, VerificationCodeHasher codeHasher, OtpSender otpSender,
            OperationalAuditStore auditStore, Clock clock
    ) {
        return new RequestCitizenPhoneVerificationService(citizenStore, accountStore, challengeStore,
                codeGenerator, codeHasher, otpSender, auditStore, clock);
    }

    @Bean
    public ConfirmCitizenPhoneVerificationUseCase confirmCitizenPhoneVerificationUseCase(
            CitizenStore citizenStore, AccountStore accountStore, EnrollmentChallengeStore challengeStore,
            VerifyEnrollmentOtpUseCase verifyEnrollmentOtpUseCase, OperationalAuditStore auditStore, Clock clock
    ) {
        return new ConfirmCitizenPhoneVerificationService(citizenStore, accountStore, challengeStore,
                verifyEnrollmentOtpUseCase, auditStore, clock);
    }
}
