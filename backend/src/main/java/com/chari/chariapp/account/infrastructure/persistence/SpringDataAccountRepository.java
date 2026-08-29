package com.chari.chariapp.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, String> {

    boolean existsByEmailLookup(String emailLookup);

    boolean existsByCitizenId(String citizenId);

    Optional<AccountJpaEntity> findByEmailLookup(String emailLookup);
}
