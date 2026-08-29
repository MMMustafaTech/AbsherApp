package com.absher.absherapp.request.infrastructure.storage;

import com.absher.absherapp.request.application.port.out.AttachmentContentStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Development storage adapter. Files have opaque generated keys and are never public URLs. */
@Component
public class LocalAttachmentContentStore implements AttachmentContentStore {

    private final Path root;

    public LocalAttachmentContentStore(@Value("${app.attachments.local.root:./var/attachments}") String root) {
        this.root = Path.of(root).toAbsolutePath().normalize();
    }

    @Override
    public void store(String storageKey, InputStream content) {
        try {
            Files.createDirectories(root);
            Path target = resolve(storageKey);
            Path temporary = Files.createTempFile(root, "upload-", ".tmp");
            try {
                Files.copy(content, temporary, StandardCopyOption.REPLACE_EXISTING);
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temporary);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to store attachment", exception);
        }
    }

    @Override
    public InputStream read(String storageKey) {
        try {
            Path file = resolve(storageKey);
            if (!Files.isRegularFile(file)) {
                throw new IllegalStateException("Attachment content is unavailable");
            }
            return Files.newInputStream(file);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read attachment", exception);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to remove attachment", exception);
        }
    }

    private Path resolve(String storageKey) {
        if (storageKey == null || !storageKey.matches("[a-f0-9-]{36}")) {
            throw new IllegalArgumentException("Invalid attachment storage key");
        }
        Path resolved = root.resolve(storageKey).normalize();
        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException("Invalid attachment storage key");
        }
        return resolved;
    }
}
