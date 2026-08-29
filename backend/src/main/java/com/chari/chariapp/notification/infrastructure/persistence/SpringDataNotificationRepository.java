package com.chari.chariapp.notification.infrastructure.persistence;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
interface SpringDataNotificationRepository extends JpaRepository<UserNotificationJpaEntity,String> { List<UserNotificationJpaEntity> findByCitizenIdOrderByCreatedAtDesc(String citizenId); List<UserNotificationJpaEntity> findByCitizenIdAndReadAtIsNullOrderByCreatedAtDesc(String citizenId); long countByCitizenIdAndReadAtIsNull(String citizenId); }
