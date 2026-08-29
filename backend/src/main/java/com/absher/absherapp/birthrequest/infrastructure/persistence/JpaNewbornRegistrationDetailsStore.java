package com.absher.absherapp.birthrequest.infrastructure.persistence;

import com.absher.absherapp.birthrequest.application.port.out.NewbornRegistrationDetailsStore;
import com.absher.absherapp.birthrequest.domain.NewbornRegistrationDetails;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaNewbornRegistrationDetailsStore implements NewbornRegistrationDetailsStore {

    private final SpringDataNewbornRegistrationDetailsRepository repository;
    private final NewbornRegistrationDetailsCipher cipher;

    public JpaNewbornRegistrationDetailsStore(SpringDataNewbornRegistrationDetailsRepository repository,
                                              NewbornRegistrationDetailsCipher cipher) {
        this.repository = repository;
        this.cipher = cipher;
    }

    @Override
    public void save(UUID requestId, NewbornRegistrationDetails details) {
        repository.save(new NewbornRegistrationDetailsJpaEntity(requestId.toString(), cipher.encrypt(details)));
    }

    @Override
    public Optional<NewbornRegistrationDetails> findByRequestId(UUID requestId) {
        return repository.findById(requestId.toString()).map(entity -> cipher.decrypt(entity.encryptedPayload()));
    }
}
