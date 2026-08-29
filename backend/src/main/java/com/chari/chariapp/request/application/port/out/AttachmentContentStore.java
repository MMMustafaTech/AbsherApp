package com.chari.chariapp.request.application.port.out;

import java.io.InputStream;

/** Storage boundary. Local disk is used in development and can later be replaced by S3 or MinIO. */
public interface AttachmentContentStore {
    void store(String storageKey, InputStream content);

    InputStream read(String storageKey);

    void delete(String storageKey);
}
