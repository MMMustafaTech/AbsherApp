package com.chari.chariapp.shared.application.port.out;

import java.time.Instant;

/** Records immutable operational events without placing personal data in the audit payload. */
public interface OperationalAuditStore {
    void record(String actorAccountId, String action, String targetType, String targetId, String metadata, Instant occurredAt);
}
