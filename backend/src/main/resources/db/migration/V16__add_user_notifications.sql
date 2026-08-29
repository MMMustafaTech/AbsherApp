CREATE TABLE user_notifications (
    id CHAR(36) PRIMARY KEY,
    citizen_id CHAR(36) NOT NULL,
    notification_type VARCHAR(64) NOT NULL,
    title VARCHAR(160) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_user_notifications_citizen FOREIGN KEY (citizen_id) REFERENCES citizens(id)
);

CREATE INDEX idx_user_notifications_citizen_created ON user_notifications(citizen_id, created_at);
CREATE INDEX idx_user_notifications_citizen_read ON user_notifications(citizen_id, read_at);
