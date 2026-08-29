package com.absher.absherapp.account.infrastructure.persistence;

import com.absher.absherapp.account.domain.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SpringDataRoleRepository extends JpaRepository<RoleJpaEntity, Long> {

    List<RoleJpaEntity> findByCodeIn(Collection<AccountRole> codes);
}
