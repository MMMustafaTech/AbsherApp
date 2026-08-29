package com.chari.chariapp.document.application.port.out;

import com.chari.chariapp.document.domain.MyBirthCertificate;
import com.chari.chariapp.document.domain.MyNationalIdentity;
import com.chari.chariapp.document.domain.MyPassport;
import com.chari.chariapp.citizen.domain.CitizenId;

import java.util.Optional;

public interface CitizenDocumentReadStore {

    Optional<MyPassport> findPassportByCitizenId(CitizenId citizenId);

    Optional<MyNationalIdentity> findNationalIdentityByCitizenId(CitizenId citizenId);

    Optional<MyBirthCertificate> findBirthCertificateByCitizenId(CitizenId citizenId);
}
