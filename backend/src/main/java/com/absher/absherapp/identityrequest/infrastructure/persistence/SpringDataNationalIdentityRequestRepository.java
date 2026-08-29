package com.absher.absherapp.identityrequest.infrastructure.persistence;

import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface SpringDataNationalIdentityRequestRepository extends JpaRepository<NationalIdentityRequestJpaEntity, String> {
    @Query("select count(r) > 0 from NationalIdentityRequestJpaEntity r where r.citizenId = :citizenId and r.type = 'NATIONAL_IDENTITY' and r.openRequestKey = :citizenId")
    boolean hasOpen(@Param("citizenId") String citizenId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from NationalIdentityRequestJpaEntity r where r.id = :id")
    Optional<NationalIdentityRequestJpaEntity> findForUpdate(@Param("id") String id);
    List<NationalIdentityRequestJpaEntity> findByTypeAndStatusOrderBySubmittedAtAsc(String type, NationalIdentityRequestStatus status);
    List<NationalIdentityRequestJpaEntity> findByTypeAndCitizenIdOrderBySubmittedAtDesc(String type, String citizenId);
}
