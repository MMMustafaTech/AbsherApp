package com.chari.chariapp.request.infrastructure;

import com.chari.chariapp.account.application.port.out.AccountStore;
import com.chari.chariapp.request.application.PassportRequestActorAccess;
import com.chari.chariapp.request.application.PassportRequestAttachmentService;
import com.chari.chariapp.request.application.PassportRequestQueryService;
import com.chari.chariapp.request.application.ReviewPassportRequestService;
import com.chari.chariapp.request.application.SubmitPassportRequestService;
import com.chari.chariapp.request.application.port.out.PassportRequestStatusHistoryStore;
import com.chari.chariapp.request.application.port.out.PassportRequestStore;
import com.chari.chariapp.request.application.port.out.AttachmentContentStore;
import com.chari.chariapp.request.application.port.out.PassportRequestAttachmentStore;
import com.chari.chariapp.shared.application.port.out.OperationalAuditStore;
import com.chari.chariapp.notification.application.NotificationService;
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
