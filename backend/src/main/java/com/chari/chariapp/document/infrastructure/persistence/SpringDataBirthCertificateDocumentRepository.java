package com.chari.chariapp.document.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SpringDataBirthCertificateDocumentRepository extends JpaRepository<BirthCertificateDocumentJpaEntity, String> {
    Optional<BirthCertificateDocumentJpaEntity> findFirstByCitizenIdOrderByRevisionDesc(String citizenId);
    boolean existsByDocumentNumberLookup(String documentNumberLookup);
}
