package com.chari.chariapp.account.domain;

import java.util.Objects;
import java.util.UUID;

public record RefreshSessionId(UUID value) {

    public RefreshSessionId {
        Objects.requireNonNull(value, "Refresh session ID is required");
    }

    public static RefreshSessionId newId() {
        return new RefreshSessionId(UUID.randomUUID());
    }
}
