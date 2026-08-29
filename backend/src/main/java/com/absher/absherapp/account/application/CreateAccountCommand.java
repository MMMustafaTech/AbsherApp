package com.absher.absherapp.account.application;

import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.citizen.domain.CitizenId;

import java.util.Set;

public record CreateAccountCommand(
        CitizenId citizenId,
        String emailLookup,
        String encryptedEmail,
        String rawPassword,
        Set<AccountRole> roles
) {
}
