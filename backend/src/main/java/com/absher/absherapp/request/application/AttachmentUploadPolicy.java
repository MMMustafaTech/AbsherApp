package com.absher.absherapp.request.application;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/** Shared validation rules for every protected service-request attachment. */
public final class AttachmentUploadPolicy {
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "application/pdf");

    private AttachmentUploadPolicy() { }

    public static String validateMetadata(AttachmentUpload upload) {
        if (upload == null || upload.content() == null || upload.sizeBytes() < 1 || upload.sizeBytes() > MAX_FILE_SIZE) {
            throw new AttachmentUploadException("Attachment size must be between 1 byte and 5 MB");
        }
        String contentType = upload.contentType() == null ? "" : upload.contentType().split(";", 2)[0].trim().toLowerCase();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) throw new AttachmentUploadException("Only JPEG, PNG and PDF attachments are allowed");
        return contentType;
    }

    public static void validateSignature(BufferedInputStream content, String contentType) throws IOException {
        content.mark(16);
        byte[] header = content.readNBytes(8);
        content.reset();
        boolean valid = switch (contentType) {
            case "image/jpeg" -> header.length >= 3 && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF;
            case "image/png" -> header.length >= 8 && header[0] == (byte) 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47 && header[4] == 0x0D && header[5] == 0x0A && header[6] == 0x1A && header[7] == 0x0A;
            case "application/pdf" -> header.length >= 5 && header[0] == '%' && header[1] == 'P' && header[2] == 'D' && header[3] == 'F' && header[4] == '-';
            default -> false;
        };
        if (!valid) throw new AttachmentUploadException("Attachment content does not match its declared type");
    }

    public static String safeFileName(String originalFileName) {
        String fileName = originalFileName == null ? "attachment" : Path.of(originalFileName.replace('\\', '/')).getFileName().toString();
        if (fileName.isBlank()) return "attachment";
        return fileName.length() > 255 ? fileName.substring(0, 255) : fileName;
    }
}
