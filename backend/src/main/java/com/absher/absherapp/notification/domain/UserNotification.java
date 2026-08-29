package com.absher.absherapp.notification.domain;

import com.absher.absherapp.citizen.domain.CitizenId;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record UserNotification(UUID id, CitizenId citizenId, NotificationType type, String title, String message,
                               Instant readAt, Instant createdAt) {
    public UserNotification {
        Objects.requireNonNull(id); Objects.requireNonNull(citizenId); Objects.requireNonNull(type); title = text(title, "Notification title", 160); message = text(message, "Notification message", 1000); Objects.requireNonNull(createdAt);
    }
    public static UserNotification create(CitizenId citizenId, NotificationType type, String title, String message, Instant now) { return new UserNotification(UUID.randomUUID(), citizenId, type, title, message, null, now); }
    public UserNotification markRead(Instant now) { return readAt == null ? new UserNotification(id, citizenId, type, title, message, now, createdAt) : this; }
    private static String text(String value, String field, int max) { String normalized=value==null?null:value.trim(); if(normalized==null||normalized.isEmpty()||normalized.length()>max) throw new IllegalArgumentException(field+" is required and must not exceed "+max+" characters"); return normalized; }
}
