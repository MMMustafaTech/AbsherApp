package com.absher.absherapp.account.application;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.citizen.domain.Citizen;
import com.absher.absherapp.shared.security.PersonalDataProtector;
import org.springframework.security.access.AccessDeniedException;

import java.util.Objects;

public class CitizenProfileService {

    private final AccountStore accountStore;
    private final CitizenStore citizenStore;
    private final PersonalDataProtector dataProtector;

    public CitizenProfileService(
            AccountStore accountStore, CitizenStore citizenStore, PersonalDataProtector dataProtector
    ) {
        this.accountStore = Objects.requireNonNull(accountStore, "Account store is required");
        this.citizenStore = Objects.requireNonNull(citizenStore, "Citizen store is required");
        this.dataProtector = Objects.requireNonNull(dataProtector, "Personal data protector is required");
    }

    public CitizenProfile profile(AccountId accountId) {
        Account account = requireActiveCitizen(accountId);
        Citizen citizen = citizenStore.findById(account.citizenId())
                .orElseThrow(() -> new AccessDeniedException("Citizen access is required"));

        String maskedPhone = citizen.verifiedPhoneOptional()
                .map(phone -> mask(dataProtector.decrypt(phone.ciphertext()), 3))
                .orElse(null);
        return new CitizenProfile(
                dataProtector.decrypt(account.email().ciphertext()),
                mask(dataProtector.decrypt(citizen.nationalId().ciphertext()), 4),
                maskedPhone,
                citizen.verifiedPhoneOptional().isPresent(),
                citizen.phoneVerifiedAt(),
                account.createdAt()
        );
    }

    private Account requireActiveCitizen(AccountId accountId) {
        return accountStore.findById(accountId)
                .filter(account -> account.status() == AccountStatus.ACTIVE)
                .filter(account -> account.roles().contains(AccountRole.CITIZEN))
                .filter(account -> account.citizenIdOptional().isPresent())
                .orElseThrow(() -> new AccessDeniedException("Citizen access is required"));
    }

    private static String mask(String value, int visibleSuffixLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Protected profile data is unavailable");
        }
        int hiddenLength = Math.max(1, value.length() - visibleSuffixLength);
        return "*".repeat(hiddenLength) + value.substring(hiddenLength);
    }
}
