package com.chari.chariapp.request.application;

import com.chari.chariapp.request.domain.PassportRequestAttachment;

import java.io.InputStream;

public record AttachmentContent(PassportRequestAttachment attachment, InputStream content) {
}
