CREATE TABLE request_attachments (
    id CHAR(36) PRIMARY KEY,
    service_request_id CHAR(36) NOT NULL,
    storage_key VARCHAR(128) NOT NULL UNIQUE,
    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(128) NOT NULL,
    size_bytes BIGINT NOT NULL,
    uploaded_by CHAR(36) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_request_attachments_request
        FOREIGN KEY (service_request_id) REFERENCES service_requests(id),
    CONSTRAINT fk_request_attachments_uploader
        FOREIGN KEY (uploaded_by) REFERENCES accounts(id)
);

CREATE INDEX idx_request_attachments_request_uploaded
    ON request_attachments(service_request_id, uploaded_at);
