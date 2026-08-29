package com.absher.absherapp.birthrequest.application.port.out;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestStatusChange;
import java.util.*;
public interface BirthCertificateRequestStatusHistoryStore { void append(BirthCertificateRequestStatusChange change); List<BirthCertificateRequestStatusChange> findByRequestId(UUID requestId); }
