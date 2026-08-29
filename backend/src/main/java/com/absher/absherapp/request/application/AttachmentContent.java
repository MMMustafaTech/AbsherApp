package com.absher.absherapp.request.application;

import com.absher.absherapp.request.domain.PassportRequestAttachment;

import java.io.InputStream;

public record AttachmentContent(PassportRequestAttachment attachment, InputStream content) {
}
