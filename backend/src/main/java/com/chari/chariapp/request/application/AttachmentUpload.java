package com.chari.chariapp.request.application;

import java.io.InputStream;

/** Framework-neutral upload input; the stream is consumed exactly once by the use case. */
public record AttachmentUpload(
        String originalFileName,
        String contentType,
        long sizeBytes,
        InputStream content
) {
}
