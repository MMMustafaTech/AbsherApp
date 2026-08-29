package com.chari.chariapp.document.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SpringDataNationalIdentityDocumentRepository extends JpaRepository<NationalIdentityDocumentJpaEntity, String> {
    Optional<NationalIdentityDocumentJpaEntity> findFirstByCitizenIdOrderByRevisionDesc(String citizenId);
    boolean existsByDocumentNumberLookup(String documentNumberLookup);
}
