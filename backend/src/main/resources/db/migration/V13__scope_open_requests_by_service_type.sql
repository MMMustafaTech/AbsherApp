ALTER TABLE service_requests
    ADD COLUMN open_request_type VARCHAR(64);

UPDATE service_requests
SET open_request_type = type
WHERE open_request_key IS NOT NULL;

DROP INDEX uq_service_requests_open_request_key ON service_requests;

CREATE UNIQUE INDEX uq_service_requests_open_request_per_type
    ON service_requests(citizen_id, open_request_type);
