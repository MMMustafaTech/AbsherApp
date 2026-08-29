package com.chari.chariapp.citizen.domain;

import java.util.Objects;
import java.util.UUID;

public record CitizenId(UUID value) {

    public CitizenId {
        Objects.requireNonNull(value, "Citizen ID is required");
    }

    public static CitizenId newId() {
        return new CitizenId(UUID.randomUUID());
    }
}
