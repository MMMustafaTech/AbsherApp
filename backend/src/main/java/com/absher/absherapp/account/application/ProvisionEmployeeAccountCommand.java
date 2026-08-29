package com.absher.absherapp.account.application;

import com.absher.absherapp.account.domain.AccountId;

public record ProvisionEmployeeAccountCommand(
        AccountId administratorId,
        String emailLookup,
        String encryptedEmail,
        String rawPassword
) {
}
