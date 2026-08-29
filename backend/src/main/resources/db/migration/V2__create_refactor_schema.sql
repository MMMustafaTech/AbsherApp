CREATE TABLE citizen_registry (
    id CHAR(36) PRIMARY KEY,
    national_id_lookup CHAR(64) NOT NULL UNIQUE,
    national_id_ciphertext TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36),
    updated_by CHAR(36),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE accounts (
    id CHAR(36) PRIMARY KEY,
    citizen_id CHAR(36) UNIQUE,
    email_lookup CHAR(64) NOT NULL UNIQUE,
    email_ciphertext TEXT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    verified_at TIMESTAMP,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_accounts_citizen
        FOREIGN KEY (citizen_id) REFERENCES citizen_registry(id)
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE account_roles (
    account_id CHAR(36) NOT NULL,
    role_id BIGINT NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    granted_by CHAR(36),
    PRIMARY KEY (account_id, role_id),
    CONSTRAINT fk_account_roles_account
        FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT fk_account_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE refresh_sessions (
    id CHAR(36) PRIMARY KEY,
    account_id CHAR(36) NOT NULL,
    token_hash CHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP,
    CONSTRAINT fk_refresh_sessions_account
        FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE verification_challenges (
    id CHAR(36) PRIMARY KEY,
    account_id CHAR(36),
    channel VARCHAR(32) NOT NULL,
    destination_lookup CHAR(64) NOT NULL,
    challenge_hash CHAR(64) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    consumed_at TIMESTAMP,
    failed_attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_verification_challenges_account
        FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE national_identity_documents (
    id CHAR(36) PRIMARY KEY,
    citizen_id CHAR(36) NOT NULL,
    document_number_lookup CHAR(64) NOT NULL UNIQUE,
    encrypted_payload TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    issued_on DATE,
    expires_on DATE,
    revision INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_national_identity_revision UNIQUE (citizen_id, revision),
    CONSTRAINT fk_national_identity_documents_citizen
        FOREIGN KEY (citizen_id) REFERENCES citizen_registry(id)
);

CREATE TABLE passport_documents (
    id CHAR(36) PRIMARY KEY,
    citizen_id CHAR(36) NOT NULL,
    passport_number_lookup CHAR(64) NOT NULL UNIQUE,
    encrypted_payload TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    issued_on DATE,
    expires_on DATE,
    revision INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_passport_revision UNIQUE (citizen_id, revision),
    CONSTRAINT fk_passport_documents_citizen
        FOREIGN KEY (citizen_id) REFERENCES citizen_registry(id)
);

CREATE TABLE birth_certificate_documents (
    id CHAR(36) PRIMARY KEY,
    citizen_id CHAR(36) NOT NULL,
    certificate_number_lookup CHAR(64) NOT NULL UNIQUE,
    encrypted_payload TEXT NOT NULL,
    revision INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by CHAR(36),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_birth_certificate_revision UNIQUE (citizen_id, revision),
    CONSTRAINT fk_birth_certificate_documents_citizen
        FOREIGN KEY (citizen_id) REFERENCES citizen_registry(id)
);

CREATE TABLE service_requests (
    id CHAR(36) PRIMARY KEY,
    citizen_id CHAR(36) NOT NULL,
    type VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    request_payload TEXT,
    submitted_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_service_requests_citizen
        FOREIGN KEY (citizen_id) REFERENCES citizen_registry(id)
);

CREATE TABLE request_status_history (
    id CHAR(36) PRIMARY KEY,
    service_request_id CHAR(36) NOT NULL,
    from_status VARCHAR(32),
    to_status VARCHAR(32) NOT NULL,
    reason VARCHAR(1000),
    changed_by CHAR(36),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_request_status_history_request
        FOREIGN KEY (service_request_id) REFERENCES service_requests(id)
);

CREATE TABLE audit_events (
    id CHAR(36) PRIMARY KEY,
    actor_account_id CHAR(36),
    action VARCHAR(128) NOT NULL,
    target_type VARCHAR(64) NOT NULL,
    target_id CHAR(36),
    result VARCHAR(32) NOT NULL,
    correlation_id VARCHAR(128),
    metadata TEXT,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_events_actor
        FOREIGN KEY (actor_account_id) REFERENCES accounts(id)
);

CREATE INDEX idx_refresh_sessions_account ON refresh_sessions(account_id);
CREATE INDEX idx_verification_challenges_destination ON verification_challenges(destination_lookup);
CREATE INDEX idx_national_identity_documents_citizen ON national_identity_documents(citizen_id);
CREATE INDEX idx_passport_documents_citizen ON passport_documents(citizen_id);
CREATE INDEX idx_birth_certificate_documents_citizen ON birth_certificate_documents(citizen_id);
CREATE INDEX idx_service_requests_citizen_status ON service_requests(citizen_id, status);
CREATE INDEX idx_request_status_history_request ON request_status_history(service_request_id);
CREATE INDEX idx_audit_events_actor_occurred ON audit_events(actor_account_id, occurred_at);
