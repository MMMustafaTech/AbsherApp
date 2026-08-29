package com.chari.chariapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RefactorSchemaMigrationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createsTheRefactorSchemaWithoutRemovingLegacyTables() {
        List<String> tableNames = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'PUBLIC'",
                String.class
        );

        assertThat(tableNames).contains(
                "CITIZENS",
                "USERS",
                "CITIZEN_REGISTRY",
                "ACCOUNTS",
                "NATIONAL_IDENTITY_DOCUMENTS",
                "PASSPORT_DOCUMENTS",
                "BIRTH_CERTIFICATE_DOCUMENTS",
                "SERVICE_REQUESTS",
                "AUDIT_EVENTS"
        );
    }
}
