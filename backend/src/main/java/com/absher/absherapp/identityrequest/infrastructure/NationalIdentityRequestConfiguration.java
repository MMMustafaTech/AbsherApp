package com.absher.absherapp.identityrequest.infrastructure;

import com.absher.absherapp.identityrequest.application.NationalIdentityRequestQueryService;
import com.absher.absherapp.identityrequest.application.NationalIdentityRequestAttachmentService;
import com.absher.absherapp.identityrequest.application.ReviewNationalIdentityRequestService;
import com.absher.absherapp.identityrequest.application.SubmitNationalIdentityRequestService;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStatusHistoryStore;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestAttachmentStore;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStore;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.request.application.port.out.AttachmentContentStore;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import com.absher.absherapp.notification.application.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class NationalIdentityRequestConfiguration {
    @Bean SubmitNationalIdentityRequestService submitNationalIdentityRequestService(PassportRequestActorAccess access, NationalIdentityRequestStore requests, NationalIdentityRequestStatusHistoryStore history, OperationalAuditStore audit, Clock clock) { return new SubmitNationalIdentityRequestService(access, requests, history, audit, clock); }
    @Bean ReviewNationalIdentityRequestService reviewNationalIdentityRequestService(PassportRequestActorAccess access, NationalIdentityRequestStore requests, NationalIdentityRequestStatusHistoryStore history, OperationalAuditStore audit, NotificationService notifications, Clock clock) { return new ReviewNationalIdentityRequestService(access, requests, history, audit, notifications, clock); }
    @Bean NationalIdentityRequestQueryService nationalIdentityRequestQueryService(PassportRequestActorAccess access, NationalIdentityRequestStore requests, NationalIdentityRequestStatusHistoryStore history) { return new NationalIdentityRequestQueryService(access, requests, history); }
    @Bean NationalIdentityRequestAttachmentService nationalIdentityRequestAttachmentService(PassportRequestActorAccess access, NationalIdentityRequestStore requests, NationalIdentityRequestAttachmentStore attachments, AttachmentContentStore contentStore, OperationalAuditStore audit, Clock clock) { return new NationalIdentityRequestAttachmentService(access, requests, attachments, contentStore, audit, clock); }
}
