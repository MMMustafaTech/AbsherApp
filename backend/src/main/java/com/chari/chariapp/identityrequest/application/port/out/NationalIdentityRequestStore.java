package com.chari.chariapp.identityrequest.application.port.out;

import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequest;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestStatus;

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
