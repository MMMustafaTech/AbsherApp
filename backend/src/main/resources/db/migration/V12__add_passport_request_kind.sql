ALTER TABLE service_requests
    ADD COLUMN request_kind VARCHAR(32) NOT NULL DEFAULT 'ISSUANCE';

ALTER TABLE service_requests
    ADD COLUMN request_reason VARCHAR(1000);
