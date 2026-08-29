package com.absher.absherapp.shared.infrastructure.development;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.application.port.out.PasswordHasher;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.account.domain.EmailReference;
import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.citizen.domain.Citizen;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.citizen.domain.NationalIdReference;
import com.absher.absherapp.citizen.domain.PhoneReference;
import com.absher.absherapp.shared.security.PersonalDataProtector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;

/** Opt-in fixtures for manually exercising local APIs. Never loaded outside the local profile. */
@Component
@Profile("local")
@ConditionalOnProperty(prefix = "app.demo-data", name = "enabled", havingValue = "true")
public class LocalDevelopmentDataInitializer implements ApplicationRunner {

    public static final String ENROLLMENT_NATIONAL_ID = "123456789";
    public static final String ENROLLMENT_PHONE = "+23590000001";
    public static final String CITIZEN_NATIONAL_ID = "987654321";
    public static final String CITIZEN_EMAIL = "citizen@local.absher.test";
    public static final String ADMIN_EMAIL = "admin@local.absher.test";
    public static final String EMPLOYEE_EMAIL = "employee@local.absher.test";
    public static final String PASSWORD = "LocalPass123!";

    private static final Logger log = LoggerFactory.getLogger(LocalDevelopmentDataInitializer.class);

    private final CitizenStore citizenStore;
    private final AccountStore accountStore;
    private final PasswordHasher passwordHasher;
    private final PersonalDataProtector dataProtector;
    private final Clock clock;

    public LocalDevelopmentDataInitializer(
            CitizenStore citizenStore,
            AccountStore accountStore,
            PasswordHasher passwordHasher,
            PersonalDataProtector dataProtector,
            Clock clock
    ) {
        this.citizenStore = citizenStore;
        this.accountStore = accountStore;
        this.passwordHasher = passwordHasher;
        this.dataProtector = dataProtector;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        ensureVerifiedCitizen(ENROLLMENT_NATIONAL_ID, ENROLLMENT_PHONE);
        Citizen citizenAccountOwner = ensureVerifiedCitizen(CITIZEN_NATIONAL_ID, "+23590000002");

        ensureAccount(CITIZEN_EMAIL, citizenAccountOwner.id(), AccountRole.CITIZEN);
        ensureAccount(ADMIN_EMAIL, null, AccountRole.ADMIN);
        ensureAccount(EMPLOYEE_EMAIL, null, AccountRole.EMPLOYEE);

        log.warn(
                "Local demo data enabled: OTP citizen {}, citizen {}, admin {}, employee {}",
                ENROLLMENT_NATIONAL_ID, CITIZEN_EMAIL, ADMIN_EMAIL, EMPLOYEE_EMAIL
        );
    }

    private Citizen ensureVerifiedCitizen(String nationalId, String phone) {
        String nationalIdLookup = dataProtector.lookup(nationalId);
        return citizenStore.findByNationalIdLookup(nationalIdLookup).orElseGet(() -> {
            Instant now = Instant.now(clock);
            return citizenStore.save(new Citizen(
                    CitizenId.newId(),
                    new NationalIdReference(nationalIdLookup, dataProtector.encrypt(nationalId)),
                    new PhoneReference(dataProtector.lookup(phone), dataProtector.encrypt(phone)),
                    now,
                    now
            ));
        });
    }

    private void ensureAccount(String email, CitizenId citizenId, AccountRole role) {
        String normalizedEmail = email.toLowerCase(Locale.ROOT);
        String emailLookup = dataProtector.lookup(normalizedEmail);
        if (accountStore.existsByEmailLookup(emailLookup)) {
            return;
        }

        accountStore.save(new Account(
                AccountId.newId(),
                citizenId,
                new EmailReference(emailLookup, dataProtector.encrypt(normalizedEmail)),
                passwordHasher.hash(PASSWORD),
                AccountStatus.ACTIVE,
                Set.of(role),
                Instant.now(clock)
        ));
    }
}
