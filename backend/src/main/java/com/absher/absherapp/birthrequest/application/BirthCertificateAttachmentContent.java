package com.absher.absherapp.birthrequest.application;

import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestAttachment;
import java.io.InputStream;

public record BirthCertificateAttachmentContent(BirthCertificateRequestAttachment attachment, InputStream content) {
}
