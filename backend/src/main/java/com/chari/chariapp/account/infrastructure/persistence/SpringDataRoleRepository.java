package com.chari.chariapp.account.infrastructure.persistence;

import com.chari.chariapp.account.domain.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SpringDataRoleRepository extends JpaRepository<RoleJpaEntity, Long> {

    List<RoleJpaEntity> findByCodeIn(Collection<AccountRole> codes);
}
