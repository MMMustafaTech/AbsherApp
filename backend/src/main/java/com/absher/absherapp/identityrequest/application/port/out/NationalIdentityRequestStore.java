package com.absher.absherapp.identityrequest.application.port.out;

import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequest;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NationalIdentityRequestStore {
    boolean hasOpenRequest(CitizenId citizenId);
    Optional<NationalIdentityRequest> findById(UUID requestId);
    Optional<NationalIdentityRequest> findByIdForUpdate(UUID requestId);
    NationalIdentityRequest save(NationalIdentityRequest request);
    List<NationalIdentityRequest> findByStatus(NationalIdentityRequestStatus status);
    List<NationalIdentityRequest> findByCitizenId(CitizenId citizenId);
}
