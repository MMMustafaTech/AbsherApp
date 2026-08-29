package com.chari.chariapp.birthrequest.application;

import com.chari.chariapp.birthrequest.domain.BirthCertificateRequestAttachment;
import java.io.InputStream;

public record BirthCertificateAttachmentContent(BirthCertificateRequestAttachment attachment, InputStream content) {
}
