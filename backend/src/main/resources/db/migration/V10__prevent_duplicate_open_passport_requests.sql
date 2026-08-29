-- A NULL value permits historical/closed requests; a citizen ID permits only one open passport request.
ALTER TABLE service_requests ADD COLUMN open_request_key CHAR(36);

UPDATE service_requests
SET open_request_key = citizen_id
WHERE type = 'PASSPORT'
  AND status IN ('SUBMITTED', 'UNDER_REVIEW');

CREATE UNIQUE INDEX uq_service_requests_open_request_key ON service_requests(open_request_key);
