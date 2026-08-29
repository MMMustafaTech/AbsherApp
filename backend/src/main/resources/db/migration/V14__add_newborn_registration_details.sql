CREATE TABLE birth_certificate_registration_details (
    service_request_id CHAR(36) PRIMARY KEY,
    encrypted_payload LONGTEXT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_birth_registration_details_request
        FOREIGN KEY (service_request_id) REFERENCES service_requests(id)
);
