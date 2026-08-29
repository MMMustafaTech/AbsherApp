package com.absher.absherapp.birthrequest.infrastructure;
import com.absher.absherapp.birthrequest.application.*;
import com.absher.absherapp.birthrequest.application.port.out.*;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.request.application.port.out.AttachmentContentStore;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import com.absher.absherapp.notification.application.NotificationService;
import org.springframework.context.annotation.*;
import java.time.Clock;
@Configuration public class BirthCertificateRequestConfiguration {
    @Bean SubmitBirthCertificateRequestService submitBirthCertificateRequestService(PassportRequestActorAccess a,BirthCertificateRequestStore r,BirthCertificateRequestStatusHistoryStore h,NewbornRegistrationDetailsStore d,OperationalAuditStore o,Clock c){return new SubmitBirthCertificateRequestService(a,r,h,d,o,c);}
    @Bean ReviewBirthCertificateRequestService reviewBirthCertificateRequestService(PassportRequestActorAccess a,BirthCertificateRequestStore r,BirthCertificateRequestStatusHistoryStore h,OperationalAuditStore o,NotificationService n,Clock c){return new ReviewBirthCertificateRequestService(a,r,h,o,n,c);}
    @Bean BirthCertificateRequestQueryService birthCertificateRequestQueryService(PassportRequestActorAccess a,BirthCertificateRequestStore r,BirthCertificateRequestStatusHistoryStore h,NewbornRegistrationDetailsStore d){return new BirthCertificateRequestQueryService(a,r,h,d);}
    @Bean BirthCertificateRequestAttachmentService birthCertificateRequestAttachmentService(PassportRequestActorAccess a,BirthCertificateRequestStore r,BirthCertificateRequestAttachmentStore s,AttachmentContentStore c,OperationalAuditStore o,Clock clock){return new BirthCertificateRequestAttachmentService(a,r,s,c,o,clock);}
}
