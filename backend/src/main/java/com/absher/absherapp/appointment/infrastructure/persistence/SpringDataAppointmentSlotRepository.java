package com.absher.absherapp.appointment.infrastructure.persistence;

import com.absher.absherapp.appointment.domain.AppointmentServiceType;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
interface SpringDataAppointmentSlotRepository extends JpaRepository<AppointmentSlotJpaEntity,String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from AppointmentSlotJpaEntity s where s.id=:id") Optional<AppointmentSlotJpaEntity> findForUpdate(@Param("id") String id);
    List<AppointmentSlotJpaEntity> findByServiceTypeAndActiveTrueAndStartsAtAfterOrderByStartsAtAsc(AppointmentServiceType type, Instant now);
}
