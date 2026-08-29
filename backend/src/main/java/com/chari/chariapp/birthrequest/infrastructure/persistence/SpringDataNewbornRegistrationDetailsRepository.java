package com.chari.chariapp.birthrequest.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataNewbornRegistrationDetailsRepository extends JpaRepository<NewbornRegistrationDetailsJpaEntity, String> {
}
