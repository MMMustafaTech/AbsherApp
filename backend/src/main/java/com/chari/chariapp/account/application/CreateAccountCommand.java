package com.chari.chariapp.account.application;

import com.chari.chariapp.account.domain.AccountRole;
import com.chari.chariapp.citizen.domain.CitizenId;

import java.util.Set;

public record CreateAccountCommand(
        CitizenId citizenId,
        String emailLookup,
        String encryptedEmail,
        String rawPassword,
        Set<AccountRole> roles
) {
}
