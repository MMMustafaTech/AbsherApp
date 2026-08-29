package com.chari.chariapp.request.application.port.out;
import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.request.domain.PassportRequest;
import com.chari.chariapp.request.domain.PassportRequestStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassportRequestStore {
    boolean hasOpenRequest(CitizenId citizenId);
    Optional<PassportRequest> findById(UUID id);
    Optional<PassportRequest> findByIdForUpdate(UUID id);
    PassportRequest save(PassportRequest request);
    List<PassportRequest> findByStatus(PassportRequestStatus status);
    List<PassportRequest> findByCitizenId(CitizenId citizenId);
}
