package com.absher.absherapp.request.infrastructure.web;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.request.application.PassportRequestQueryService;
import com.absher.absherapp.request.application.AttachmentContent;
import com.absher.absherapp.request.application.AttachmentUpload;
import com.absher.absherapp.request.application.PassportRequestAttachmentService;
import com.absher.absherapp.request.application.ReviewPassportRequestService;
import com.absher.absherapp.request.application.SubmitPassportRequestService;
import com.absher.absherapp.request.domain.PassportRequest;
import com.absher.absherapp.request.domain.PassportRequestStatus;
import com.absher.absherapp.request.domain.PassportRequestKind;
import com.absher.absherapp.request.domain.PassportRequestStatusChange;
import com.absher.absherapp.request.domain.PassportRequestAttachment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class PassportRequestController {

    private final SubmitPassportRequestService submitService;
    private final ReviewPassportRequestService reviewService;
    private final PassportRequestQueryService queryService;
    private final PassportRequestAttachmentService attachmentService;

    public PassportRequestController(
            SubmitPassportRequestService submitService,
            ReviewPassportRequestService reviewService,
            PassportRequestQueryService queryService,
            PassportRequestAttachmentService attachmentService
    ) {
        this.submitService = submitService;
        this.reviewService = reviewService;
        this.queryService = queryService;
        this.attachmentService = attachmentService;
    }

    @PostMapping("/api/v1/me/passport-requests")
    ResponseEntity<PassportRequest> submit(
            @Valid @RequestBody(required = false) Submission body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        PassportRequestKind kind = body == null || body.kind() == null ? PassportRequestKind.ISSUANCE : body.kind();
        String reason = body == null ? null : body.reason();
        return ResponseEntity.status(HttpStatus.CREATED).body(submitService.submit(accountId(jwt), kind, reason));
    }

    @GetMapping("/api/v1/me/passport-requests")
    List<PassportRequest> mine(@AuthenticationPrincipal Jwt jwt) {
        return queryService.mine(accountId(jwt));
    }

    @GetMapping("/api/v1/me/passport-requests/{requestId}/history")
    List<PassportRequestStatusChange> history(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt) {
        return queryService.history(accountId(jwt), requestId);
    }

    @PostMapping(value = "/api/v1/me/passport-requests/{requestId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<AttachmentResponse> uploadAttachment(
            @PathVariable UUID requestId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt
    ) {
        PassportRequestAttachment attachment = attachmentService.upload(accountId(jwt), requestId,
                new AttachmentUpload(file.getOriginalFilename(), file.getContentType(), file.getSize(), inputStream(file)));
        return ResponseEntity.status(HttpStatus.CREATED).body(AttachmentResponse.from(attachment));
    }

    @GetMapping("/api/v1/me/passport-requests/{requestId}/attachments")
    List<AttachmentResponse> myAttachments(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt) {
        return attachmentService.listMine(accountId(jwt), requestId).stream().map(AttachmentResponse::from).toList();
    }

    @GetMapping("/api/v1/me/passport-requests/{requestId}/attachments/{attachmentId}/content")
    ResponseEntity<InputStreamResource> myAttachmentContent(
            @PathVariable UUID requestId,
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return contentResponse(attachmentService.downloadMine(accountId(jwt), requestId, attachmentId));
    }

    @GetMapping("/api/v1/operations/passport-requests")
    List<PassportRequest> list(
            @RequestParam(defaultValue = "SUBMITTED") PassportRequestStatus status,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return queryService.byStatus(accountId(jwt), status);
    }

    @GetMapping("/api/v1/operations/passport-requests/{requestId}/attachments")
    List<AttachmentResponse> operationAttachments(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt) {
        return attachmentService.listForOperations(accountId(jwt), requestId).stream().map(AttachmentResponse::from).toList();
    }

    @GetMapping("/api/v1/operations/passport-requests/{requestId}/attachments/{attachmentId}/content")
    ResponseEntity<InputStreamResource> operationAttachmentContent(
            @PathVariable UUID requestId,
            @PathVariable UUID attachmentId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return contentResponse(attachmentService.downloadForOperations(accountId(jwt), requestId, attachmentId));
    }

    @PostMapping("/api/v1/operations/passport-requests/{requestId}/review")
    PassportRequest review(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt) {
        return reviewService.startReview(accountId(jwt), requestId);
    }

    @PostMapping("/api/v1/operations/passport-requests/{requestId}/decision")
    PassportRequest decide(
            @PathVariable UUID requestId,
            @Valid @RequestBody Decision body,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return reviewService.decide(accountId(jwt), requestId, body.approved(), body.reason());
    }

    private static AccountId accountId(Jwt jwt) {
        return new AccountId(UUID.fromString(jwt.getSubject()));
    }

    private static java.io.InputStream inputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (java.io.IOException exception) {
            throw new IllegalArgumentException("Unable to read attachment", exception);
        }
    }

    private static ResponseEntity<InputStreamResource> contentResponse(AttachmentContent content) {
        PassportRequestAttachment attachment = content.attachment();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.contentType()))
                .contentLength(attachment.sizeBytes())
                .header("X-Content-Type-Options", "nosniff")
                .header("Cache-Control", "private, no-store")
                .header("Content-Disposition", ContentDisposition.attachment().filename(attachment.originalFileName()).build().toString())
                .body(new InputStreamResource(content.content()));
    }

    public record AttachmentResponse(
            UUID id,
            String fileName,
            String contentType,
            long sizeBytes,
            java.time.Instant uploadedAt
    ) {
        static AttachmentResponse from(PassportRequestAttachment attachment) {
            return new AttachmentResponse(attachment.id(), attachment.originalFileName(), attachment.contentType(),
                    attachment.sizeBytes(), attachment.uploadedAt());
        }
    }

    public record Decision(boolean approved, @Size(max = 1000) String reason) {
    }

    public record Submission(PassportRequestKind kind, @Size(max = 1000) String reason) {
    }
}
