package com.absher.absherapp.birthrequest.infrastructure.web;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.birthrequest.application.BirthCertificateRequestQueryService;
import com.absher.absherapp.birthrequest.application.BirthCertificateRequestAttachmentService;
import com.absher.absherapp.birthrequest.application.BirthCertificateAttachmentContent;
import com.absher.absherapp.request.application.AttachmentUpload;
import com.absher.absherapp.birthrequest.application.ReviewBirthCertificateRequestService;
import com.absher.absherapp.birthrequest.application.SubmitBirthCertificateRequestService;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequest;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestKind;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestStatus;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestStatusChange;
import com.absher.absherapp.birthrequest.domain.NewbornRegistrationDetails;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestAttachment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class BirthCertificateRequestController {

    private final SubmitBirthCertificateRequestService submitService;
    private final ReviewBirthCertificateRequestService reviewService;
    private final BirthCertificateRequestQueryService queryService;
    private final BirthCertificateRequestAttachmentService attachmentService;

    public BirthCertificateRequestController(SubmitBirthCertificateRequestService submitService,
                                             ReviewBirthCertificateRequestService reviewService,
                                             BirthCertificateRequestQueryService queryService,
                                             BirthCertificateRequestAttachmentService attachmentService) {
        this.submitService = submitService;
        this.reviewService = reviewService;
        this.queryService = queryService;
        this.attachmentService = attachmentService;
    }

    @PostMapping("/me/birth-certificate-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public BirthCertificateRequest submit(@AuthenticationPrincipal Jwt jwt,
                                          @Valid @RequestBody Submission body) {
        return submitService.submit(accountId(jwt), body.kind(), body.reason(), body.newbornRegistration());
    }

    @GetMapping("/me/birth-certificate-requests")
    public List<BirthCertificateRequest> myRequests(@AuthenticationPrincipal Jwt jwt) {
        return queryService.mine(accountId(jwt));
    }

    @GetMapping("/me/birth-certificate-requests/{requestId}/history")
    public List<BirthCertificateRequestStatusChange> myHistory(@AuthenticationPrincipal Jwt jwt,
                                                                @PathVariable UUID requestId) {
        return queryService.history(accountId(jwt), requestId);
    }

    @GetMapping("/me/birth-certificate-requests/{requestId}/newborn-registration")
    public NewbornRegistrationDetails myNewbornRegistration(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable UUID requestId) {
        return queryService.myNewbornRegistration(accountId(jwt), requestId);
    }

    @PostMapping(value = "/me/birth-certificate-requests/{requestId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponse uploadAttachment(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId,
                                               @RequestPart("file") MultipartFile file) {
        BirthCertificateRequestAttachment attachment = attachmentService.upload(accountId(jwt), requestId,
                new AttachmentUpload(file.getOriginalFilename(), file.getContentType(), file.getSize(), inputStream(file)));
        return AttachmentResponse.from(attachment);
    }

    @GetMapping("/me/birth-certificate-requests/{requestId}/attachments")
    public List<AttachmentResponse> myAttachments(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId) {
        return attachmentService.listMine(accountId(jwt), requestId).stream().map(AttachmentResponse::from).toList();
    }

    @GetMapping("/me/birth-certificate-requests/{requestId}/attachments/{attachmentId}/content")
    public ResponseEntity<InputStreamResource> myAttachmentContent(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId,
                                                                    @PathVariable UUID attachmentId) {
        return attachmentResponse(attachmentService.downloadMine(accountId(jwt), requestId, attachmentId));
    }

    @GetMapping("/operations/birth-certificate-requests")
    public List<BirthCertificateRequest> operationalRequests(@AuthenticationPrincipal Jwt jwt,
                                                              @RequestParam BirthCertificateRequestStatus status) {
        return queryService.byStatus(accountId(jwt), status);
    }

    @GetMapping("/operations/birth-certificate-requests/{requestId}/newborn-registration")
    public NewbornRegistrationDetails operationalNewbornRegistration(@AuthenticationPrincipal Jwt jwt,
                                                                      @PathVariable UUID requestId) {
        return queryService.operationalNewbornRegistration(accountId(jwt), requestId);
    }

    @GetMapping("/operations/birth-certificate-requests/{requestId}/attachments")
    public List<AttachmentResponse> operationalAttachments(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId) {
        return attachmentService.listForOperations(accountId(jwt), requestId).stream().map(AttachmentResponse::from).toList();
    }

    @GetMapping("/operations/birth-certificate-requests/{requestId}/attachments/{attachmentId}/content")
    public ResponseEntity<InputStreamResource> operationalAttachmentContent(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId,
                                                                             @PathVariable UUID attachmentId) {
        return attachmentResponse(attachmentService.downloadForOperations(accountId(jwt), requestId, attachmentId));
    }

    @PostMapping("/operations/birth-certificate-requests/{requestId}/review")
    public BirthCertificateRequest startReview(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId) {
        return reviewService.startReview(accountId(jwt), requestId);
    }

    @PostMapping("/operations/birth-certificate-requests/{requestId}/decision")
    public BirthCertificateRequest decide(@AuthenticationPrincipal Jwt jwt,
                                          @PathVariable UUID requestId,
                                          @Valid @RequestBody Decision body) {
        return reviewService.decide(accountId(jwt), requestId, body.approved(), body.reason());
    }

    private AccountId accountId(Jwt jwt) {
        return new AccountId(UUID.fromString(jwt.getSubject()));
    }

    private static java.io.InputStream inputStream(MultipartFile file) {
        try { return file.getInputStream(); }
        catch (java.io.IOException exception) { throw new IllegalArgumentException("Unable to read attachment", exception); }
    }

    private static ResponseEntity<InputStreamResource> attachmentResponse(BirthCertificateAttachmentContent content) {
        BirthCertificateRequestAttachment attachment = content.attachment();
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(attachment.contentType())).contentLength(attachment.sizeBytes())
                .header("X-Content-Type-Options", "nosniff").header("Cache-Control", "private, no-store")
                .header("Content-Disposition", ContentDisposition.attachment().filename(attachment.originalFileName()).build().toString())
                .body(new InputStreamResource(content.content()));
    }

    public record AttachmentResponse(UUID id, String fileName, String contentType, long sizeBytes, java.time.Instant uploadedAt) {
        static AttachmentResponse from(BirthCertificateRequestAttachment attachment) { return new AttachmentResponse(attachment.id(), attachment.originalFileName(), attachment.contentType(), attachment.sizeBytes(), attachment.uploadedAt()); }
    }

    public record Submission(@NotNull BirthCertificateRequestKind kind,
                             @Size(max = 1000) String reason,
                             @Valid NewbornRegistrationDetails newbornRegistration) {
    }

    public record Decision(boolean approved, @Size(max = 1000) String reason) {
    }
}
