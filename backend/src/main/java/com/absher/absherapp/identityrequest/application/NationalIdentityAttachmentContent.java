package com.absher.absherapp.identityrequest.application;

import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestAttachment;
import java.io.InputStream;

public record NationalIdentityAttachmentContent(NationalIdentityRequestAttachment attachment, InputStream content) { }
