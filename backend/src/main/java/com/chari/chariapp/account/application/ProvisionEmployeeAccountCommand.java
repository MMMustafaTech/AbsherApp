package com.chari.chariapp.account.application;

import com.chari.chariapp.account.domain.AccountId;

public record ProvisionEmployeeAccountCommand(
        AccountId administratorId,
        String emailLookup,
        String encryptedEmail,
        String rawPassword
) {
}
