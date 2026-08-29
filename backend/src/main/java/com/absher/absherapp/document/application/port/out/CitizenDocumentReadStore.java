package com.absher.absherapp.document.application.port.out;

import com.absher.absherapp.document.domain.MyBirthCertificate;
import com.absher.absherapp.document.domain.MyNationalIdentity;
import com.absher.absherapp.document.domain.MyPassport;
import com.absher.absherapp.citizen.domain.CitizenId;

import java.util.Optional;

public interface CitizenDocumentReadStore {

    Optional<MyPassport> findPassportByCitizenId(CitizenId citizenId);

    Optional<MyNationalIdentity> findNationalIdentityByCitizenId(CitizenId citizenId);

    Optional<MyBirthCertificate> findBirthCertificateByCitizenId(CitizenId citizenId);
}
