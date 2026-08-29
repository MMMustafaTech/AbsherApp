package com.chari.chariapp.account.domain;

import java.util.Objects;
import java.util.UUID;

public record AccountId(UUID value) {

    public AccountId {
        Objects.requireNonNull(value, "Account ID is required");
    }

    public static AccountId newId() {
        return new AccountId(UUID.randomUUID());
    }
}
