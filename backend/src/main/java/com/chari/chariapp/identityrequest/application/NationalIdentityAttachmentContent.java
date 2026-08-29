package com.chari.chariapp.identityrequest.application;

import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestAttachment;
import java.io.InputStream;

public record NationalIdentityAttachmentContent(NationalIdentityRequestAttachment attachment, InputStream content) { }
