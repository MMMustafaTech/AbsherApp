package com.chari.chariapp.birthrequest.application.port.out;
import com.chari.chariapp.birthrequest.domain.BirthCertificateRequestStatusChange;
import java.util.*;
public interface BirthCertificateRequestStatusHistoryStore { void append(BirthCertificateRequestStatusChange change); List<BirthCertificateRequestStatusChange> findByRequestId(UUID requestId); }
