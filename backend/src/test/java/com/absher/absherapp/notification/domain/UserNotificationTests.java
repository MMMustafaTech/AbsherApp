package com.absher.absherapp.notification.domain;

import com.absher.absherapp.citizen.domain.CitizenId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserNotificationTests {

    private static final Instant NOW = Instant.parse("2026-08-29T00:00:00Z");

    @Test
    void createsUnreadNotificationAndMarksItReadOnlyOnce() {
        UserNotification notification = UserNotification.create(
                CitizenId.newId(),
                NotificationType.PASSPORT_REQUEST_APPROVED,
                " Passport request approved ",
                " Your passport request has been approved. ",
                NOW
        );

        UserNotification read = notification.markRead(NOW.plusSeconds(30));

        assertThat(notification.readAt()).isNull();
        assertThat(notification.title()).isEqualTo("Passport request approved");
        assertThat(read.readAt()).isEqualTo(NOW.plusSeconds(30));
        assertThat(read.markRead(NOW.plusSeconds(60))).isSameAs(read);
    }

    @Test
    void rejectsBlankOrOversizedVisibleText() {
        assertThatThrownBy(() -> UserNotification.create(
                CitizenId.newId(), NotificationType.APPOINTMENT_BOOKED, " ", "Message", NOW
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> UserNotification.create(
                CitizenId.newId(), NotificationType.APPOINTMENT_BOOKED, "Title", "x".repeat(1001), NOW
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
