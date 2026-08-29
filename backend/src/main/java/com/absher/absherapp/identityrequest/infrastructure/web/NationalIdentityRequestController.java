package com.absher.absherapp.identityrequest.infrastructure.web;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.identityrequest.application.NationalIdentityRequestQueryService;
import com.absher.absherapp.identityrequest.application.NationalIdentityRequestAttachmentService;
import com.absher.absherapp.identityrequest.application.NationalIdentityAttachmentContent;
import com.absher.absherapp.identityrequest.application.ReviewNationalIdentityRequestService;
import com.absher.absherapp.identityrequest.application.SubmitNationalIdentityRequestService;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequest;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestKind;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatus;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatusChange;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestAttachment;
import com.absher.absherapp.request.application.AttachmentUpload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class NationalIdentityRequestController {
    private final SubmitNationalIdentityRequestService submitService;
    private final ReviewNationalIdentityRequestService reviewService;
    private final NationalIdentityRequestQueryService queryService;
    private final NationalIdentityRequestAttachmentService attachmentService;

    public NationalIdentityRequestController(SubmitNationalIdentityRequestService submitService,
                                             ReviewNationalIdentityRequestService reviewService,
                                             NationalIdentityRequestQueryService queryService,
                                             NationalIdentityRequestAttachmentService attachmentService) {
        this.submitService = submitService; this.reviewService = reviewService; this.queryService = queryService; this.attachmentService = attachmentService;
    }

    @PostMapping("/api/v1/me/national-identity-requests")
    ResponseEntity<NationalIdentityRequest> submit(@Valid @RequestBody Submission body, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(submitService.submit(accountId(jwt), body.kind(), body.reason()));
    }

    @GetMapping("/api/v1/me/national-identity-requests")
    List<NationalIdentityRequest> mine(@AuthenticationPrincipal Jwt jwt) { return queryService.mine(accountId(jwt)); }

    @GetMapping("/api/v1/me/national-identity-requests/{requestId}/history")
    List<NationalIdentityRequestStatusChange> history(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt) { return queryService.history(accountId(jwt), requestId); }

    @PostMapping(value = "/api/v1/me/national-identity-requests/{requestId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<AttachmentResponse> uploadAttachment(@PathVariable UUID requestId, @RequestPart("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
        NationalIdentityRequestAttachment attachment = attachmentService.upload(accountId(jwt), requestId, new AttachmentUpload(file.getOriginalFilename(), file.getContentType(), file.getSize(), inputStream(file)));
        return ResponseEntity.status(HttpStatus.CREATED).body(AttachmentResponse.from(attachment));
    }

    @GetMapping("/api/v1/me/national-identity-requests/{requestId}/attachments")
    List<AttachmentResponse> myAttachments(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt) { return attachmentService.listMine(accountId(jwt), requestId).stream().map(AttachmentResponse::from).toList(); }

    @GetMapping("/api/v1/me/national-identity-requests/{requestId}/attachments/{attachmentId}/content")
    ResponseEntity<InputStreamResource> myAttachmentContent(@PathVariable UUID requestId, @PathVariable UUID attachmentId, @AuthenticationPrincipal Jwt jwt) { return attachmentResponse(attachmentService.downloadMine(accountId(jwt), requestId, attachmentId)); }

    @GetMapping("/api/v1/operations/national-identity-requests")
    List<NationalIdentityRequest> list(@RequestParam(defaultValue = "SUBMITTED") NationalIdentityRequestStatus status, @AuthenticationPrincipal Jwt jwt) { return queryService.byStatus(accountId(jwt), status); }

    @GetMapping("/api/v1/operations/national-identity-requests/{requestId}/attachments")
    List<AttachmentResponse> operationalAttachments(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt) { return attachmentService.listForOperations(accountId(jwt), requestId).stream().map(AttachmentResponse::from).toList(); }

    @GetMapping("/api/v1/operations/national-identity-requests/{requestId}/attachments/{attachmentId}/content")
    ResponseEntity<InputStreamResource> operationalAttachmentContent(@PathVariable UUID requestId, @PathVariable UUID attachmentId, @AuthenticationPrincipal Jwt jwt) { return attachmentResponse(attachmentService.downloadForOperations(accountId(jwt), requestId, attachmentId)); }

    @PostMapping("/api/v1/operations/national-identity-requests/{requestId}/review")
    NationalIdentityRequest review(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt) { return reviewService.startReview(accountId(jwt), requestId); }

    @PostMapping("/api/v1/operations/national-identity-requests/{requestId}/decision")
    NationalIdentityRequest decide(@PathVariable UUID requestId, @Valid @RequestBody Decision body, @AuthenticationPrincipal Jwt jwt) { return reviewService.decide(accountId(jwt), requestId, body.approved(), body.reason()); }

    private static AccountId accountId(Jwt jwt) { return new AccountId(UUID.fromString(jwt.getSubject())); }
    private static java.io.InputStream inputStream(MultipartFile file) { try { return file.getInputStream(); } catch (java.io.IOException exception) { throw new IllegalArgumentException("Unable to read attachment", exception); } }
    private static ResponseEntity<InputStreamResource> attachmentResponse(NationalIdentityAttachmentContent content) { NationalIdentityRequestAttachment attachment = content.attachment(); return ResponseEntity.ok().contentType(MediaType.parseMediaType(attachment.contentType())).contentLength(attachment.sizeBytes()).header("X-Content-Type-Options", "nosniff").header("Cache-Control", "private, no-store").header("Content-Disposition", ContentDisposition.attachment().filename(attachment.originalFileName()).build().toString()).body(new InputStreamResource(content.content())); }
    public record AttachmentResponse(UUID id, String fileName, String contentType, long sizeBytes, java.time.Instant uploadedAt) { static AttachmentResponse from(NationalIdentityRequestAttachment attachment) { return new AttachmentResponse(attachment.id(), attachment.originalFileName(), attachment.contentType(), attachment.sizeBytes(), attachment.uploadedAt()); } }
    public record Submission(NationalIdentityRequestKind kind, @Size(max = 1000) String reason) { }
    public record Decision(boolean approved, @Size(max = 1000) String reason) { }
}
