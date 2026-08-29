package com.absher.absherapp.request.infrastructure.persistence;

import com.absher.absherapp.request.domain.PassportRequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataPassportRequestRepository extends JpaRepository<PassportRequestJpaEntity, String> {

    @Query("""
            select count(r) > 0
            from PassportRequestJpaEntity r
            where r.citizenId = :citizenId
              and r.type = 'PASSPORT'
              and r.openRequestKey = :citizenId
            """)
    boolean hasOpen(@Param("citizenId") String citizenId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from PassportRequestJpaEntity r where r.id = :id")
    Optional<PassportRequestJpaEntity> findForUpdate(@Param("id") String id);

    List<PassportRequestJpaEntity> findByTypeAndStatusOrderBySubmittedAtAsc(String type, PassportRequestStatus status);

    List<PassportRequestJpaEntity> findByTypeAndCitizenIdOrderBySubmittedAtDesc(String type, String citizenId);
}
