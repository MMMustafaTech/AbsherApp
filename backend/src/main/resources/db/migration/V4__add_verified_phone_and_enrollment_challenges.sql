ALTER TABLE citizen_registry ADD COLUMN phone_lookup CHAR(64);
ALTER TABLE citizen_registry ADD COLUMN phone_ciphertext TEXT;
ALTER TABLE citizen_registry ADD COLUMN phone_verified_at TIMESTAMP;
CREATE UNIQUE INDEX uq_citizen_registry_phone_lookup ON citizen_registry(phone_lookup);

ALTER TABLE verification_challenges ADD COLUMN citizen_id CHAR(36);
ALTER TABLE verification_challenges ADD COLUMN purpose VARCHAR(32) NOT NULL DEFAULT 'ACCOUNT_ENROLLMENT';
ALTER TABLE verification_challenges ADD COLUMN verified_at TIMESTAMP;
ALTER TABLE verification_challenges ADD COLUMN enrollment_consumed_at TIMESTAMP;
ALTER TABLE verification_challenges ADD CONSTRAINT fk_verification_challenges_citizen
    FOREIGN KEY (citizen_id) REFERENCES citizen_registry(id);

CREATE INDEX idx_verification_challenges_citizen ON verification_challenges(citizen_id);
