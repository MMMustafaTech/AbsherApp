package com.absher.absherapp.request.infrastructure;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.request.application.PassportRequestAttachmentService;
import com.absher.absherapp.request.application.PassportRequestQueryService;
import com.absher.absherapp.request.application.ReviewPassportRequestService;
import com.absher.absherapp.request.application.SubmitPassportRequestService;
import com.absher.absherapp.request.application.port.out.PassportRequestStatusHistoryStore;
import com.absher.absherapp.request.application.port.out.PassportRequestStore;
import com.absher.absherapp.request.application.port.out.AttachmentContentStore;
import com.absher.absherapp.request.application.port.out.PassportRequestAttachmentStore;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import com.absher.absherapp.notification.application.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class RequestConfiguration {

    @Bean
    PassportRequestActorAccess passportRequestActorAccess(AccountStore accountStore) {
        return new PassportRequestActorAccess(accountStore);
    }

    @Bean
    SubmitPassportRequestService submitPassportRequestService(
            PassportRequestActorAccess actorAccess,
            PassportRequestStore requestStore,
            PassportRequestStatusHistoryStore historyStore,
            OperationalAuditStore auditStore,
            Clock clock
    ) {
        return new SubmitPassportRequestService(actorAccess, requestStore, historyStore, auditStore, clock);
    }

    @Bean
    ReviewPassportRequestService reviewPassportRequestService(
            PassportRequestActorAccess actorAccess,
            PassportRequestStore requestStore,
            PassportRequestStatusHistoryStore historyStore,
            OperationalAuditStore auditStore,
            NotificationService notifications,
            Clock clock
    ) {
        return new ReviewPassportRequestService(actorAccess, requestStore, historyStore, auditStore, notifications, clock);
    }

    @Bean
    PassportRequestQueryService passportRequestQueryService(
            PassportRequestActorAccess actorAccess,
            PassportRequestStore requestStore,
            PassportRequestStatusHistoryStore historyStore
    ) {
        return new PassportRequestQueryService(actorAccess, requestStore, historyStore);
    }

    @Bean
    PassportRequestAttachmentService passportRequestAttachmentService(
            PassportRequestActorAccess actorAccess,
            PassportRequestStore requestStore,
            PassportRequestAttachmentStore attachmentStore,
            AttachmentContentStore contentStore,
            OperationalAuditStore auditStore,
            Clock clock
    ) {
        return new PassportRequestAttachmentService(
                actorAccess, requestStore, attachmentStore, contentStore, auditStore, clock
        );
    }
}
