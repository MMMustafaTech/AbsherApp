ALTER TABLE service_requests ADD COLUMN reviewed_by CHAR(36);
ALTER TABLE service_requests ADD COLUMN reviewed_at TIMESTAMP;
ALTER TABLE service_requests ADD COLUMN decision_reason VARCHAR(1000);
CREATE INDEX idx_service_requests_type_status ON service_requests(type, status);
