package com.chari.chariapp.birthrequest.application.port.out;

import com.chari.chariapp.birthrequest.domain.NewbornRegistrationDetails;
import java.util.Optional;
import java.util.UUID;

public interface NewbornRegistrationDetailsStore {
    void save(UUID requestId, NewbornRegistrationDetails details);
    Optional<NewbornRegistrationDetails> findByRequestId(UUID requestId);
}
