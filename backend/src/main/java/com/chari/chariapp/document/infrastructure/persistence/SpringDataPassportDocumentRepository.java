package com.chari.chariapp.document.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SpringDataPassportDocumentRepository extends JpaRepository<PassportDocumentJpaEntity, String> {
    Optional<PassportDocumentJpaEntity> findFirstByCitizenIdOrderByRevisionDesc(String citizenId);
    boolean existsByDocumentNumberLookup(String documentNumberLookup);
}
