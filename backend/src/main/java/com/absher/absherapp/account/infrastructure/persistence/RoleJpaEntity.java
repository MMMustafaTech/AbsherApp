package com.absher.absherapp.account.infrastructure.persistence;

import com.absher.absherapp.account.domain.AccountRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class RoleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 64)
    private AccountRole code;

    @Column(nullable = false, length = 255)
    private String description;

    protected RoleJpaEntity() {
    }

    public AccountRole getCode() {
        return code;
    }
}
