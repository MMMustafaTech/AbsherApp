package com.absher.absherapp.appointment.infrastructure.persistence;
import com.absher.absherapp.appointment.domain.*; import jakarta.persistence.LockModeType; import java.util.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
interface SpringDataAppointmentRepository extends JpaRepository<AppointmentJpaEntity,String> {
 @Query("select count(a)>0 from AppointmentJpaEntity a where a.citizenId=:citizenId and a.activeBookingService=:service") boolean hasActive(@Param("citizenId") String citizenId,@Param("service") String service);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from AppointmentJpaEntity a where a.id=:id") Optional<AppointmentJpaEntity> findForUpdate(@Param("id") String id);
 List<AppointmentJpaEntity> findByCitizenIdOrderByBookedAtDesc(String citizenId);
 List<AppointmentJpaEntity> findByStatusOrderByStartsAtAsc(AppointmentStatus status);
}
