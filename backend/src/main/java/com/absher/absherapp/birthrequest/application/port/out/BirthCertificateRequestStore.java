package com.absher.absherapp.birthrequest.application.port.out;
import com.absher.absherapp.birthrequest.domain.*;
import com.absher.absherapp.citizen.domain.CitizenId;
import java.util.*;
public interface BirthCertificateRequestStore { boolean hasOpenRequest(CitizenId citizenId); Optional<BirthCertificateRequest> findById(UUID id); Optional<BirthCertificateRequest> findByIdForUpdate(UUID id); BirthCertificateRequest save(BirthCertificateRequest request); List<BirthCertificateRequest> findByStatus(BirthCertificateRequestStatus status); List<BirthCertificateRequest> findByCitizenId(CitizenId citizenId); }
