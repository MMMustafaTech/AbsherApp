package com.absher.absherapp.request.application.port.out;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.request.domain.PassportRequest;
import com.absher.absherapp.request.domain.PassportRequestStatus;
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
