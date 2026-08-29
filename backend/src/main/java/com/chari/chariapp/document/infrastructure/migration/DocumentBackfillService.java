package com.chari.chariapp.document.infrastructure.migration;

import com.chari.chariapp.citizen.domain.Citizen;
import com.chari.chariapp.citizen.infrastructure.persistence.SpringDataCitizenRepository;
import com.chari.chariapp.document.infrastructure.persistence.BirthCertificateDocumentJpaEntity;
import com.chari.chariapp.document.infrastructure.persistence.EncryptedDocumentPayloadCodec;
import com.chari.chariapp.document.infrastructure.persistence.NationalIdentityDocumentJpaEntity;
import com.chari.chariapp.document.infrastructure.persistence.PassportDocumentJpaEntity;
import com.chari.chariapp.document.infrastructure.persistence.SpringDataBirthCertificateDocumentRepository;
import com.chari.chariapp.document.infrastructure.persistence.SpringDataNationalIdentityDocumentRepository;
import com.chari.chariapp.document.infrastructure.persistence.SpringDataPassportDocumentRepository;
import com.chari.chariapp.entity.BirthCertificate;
import com.chari.chariapp.entity.NationalIdentity;
import com.chari.chariapp.entity.Passport;
import com.chari.chariapp.repository.BirthCertificateRepository;
import com.chari.chariapp.repository.NationalIdRepository;
import com.chari.chariapp.repository.PassportRepository;
import com.chari.chariapp.shared.security.PersonalDataProtector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Explicit, idempotent migration from legacy plaintext document tables to encrypted document tables. */
@Service
public class DocumentBackfillService {
    private final SpringDataCitizenRepository citizenRepository;
    private final PassportRepository legacyPassports;
    private final NationalIdRepository legacyNationalIdentities;
    private final BirthCertificateRepository legacyBirthCertificates;
    private final SpringDataPassportDocumentRepository passports;
    private final SpringDataNationalIdentityDocumentRepository nationalIdentities;
    private final SpringDataBirthCertificateDocumentRepository birthCertificates;
    private final EncryptedDocumentPayloadCodec payloadCodec;
    private final PersonalDataProtector dataProtector;

    public DocumentBackfillService(
            SpringDataCitizenRepository citizenRepository,
            PassportRepository legacyPassports,
            NationalIdRepository legacyNationalIdentities,
            BirthCertificateRepository legacyBirthCertificates,
            SpringDataPassportDocumentRepository passports,
            SpringDataNationalIdentityDocumentRepository nationalIdentities,
            SpringDataBirthCertificateDocumentRepository birthCertificates,
            EncryptedDocumentPayloadCodec payloadCodec,
            PersonalDataProtector dataProtector
    ) {
        this.citizenRepository = citizenRepository;
        this.legacyPassports = legacyPassports;
        this.legacyNationalIdentities = legacyNationalIdentities;
        this.legacyBirthCertificates = legacyBirthCertificates;
        this.passports = passports;
        this.nationalIdentities = nationalIdentities;
        this.birthCertificates = birthCertificates;
        this.payloadCodec = payloadCodec;
        this.dataProtector = dataProtector;
    }

    @Transactional
    public BackfillReport backfillAll() {
        int created = 0;
        int skipped = 0;
        int citizensVisited = 0;
        for (Citizen citizen : citizenRepository.findAll().stream().map(entity -> entity.toDomain()).toList()) {
            citizensVisited++;
            BackfillReport report = backfillCitizen(citizen);
            created += report.documentsCreated();
            skipped += report.documentsSkipped();
        }
        return new BackfillReport(citizensVisited, created, skipped);
    }

    @Transactional
    public BackfillReport backfillCitizen(Citizen citizen) {
        String nationalId = dataProtector.decrypt(citizen.nationalId().ciphertext());
        int created = 0;
        int skipped = 0;
        Instant now = Instant.now();

        var passport = legacyPassports.findByNationalIdNumber(nationalId);
        if (passport.isPresent()) {
            if (isBlank(passport.get().getPassportNumber())) skipped++; else if (copyPassport(citizen, passport.get(), now)) created++;
        }
        var identity = legacyNationalIdentities.findByNationalIdNumber(nationalId);
        if (identity.isPresent()) {
            if (isBlank(identity.get().getNationalIdNumber())) skipped++; else if (copyNationalIdentity(citizen, identity.get(), now)) created++;
        }
        var certificate = legacyBirthCertificates.findByNationalId(nationalId);
        if (certificate.isPresent()) {
            if (isBlank(certificate.get().getCertificateNumber())) skipped++; else if (copyBirthCertificate(citizen, certificate.get(), now)) created++;
        }
        return new BackfillReport(1, created, skipped);
    }

    private boolean copyPassport(Citizen citizen, Passport value, Instant now) {
        String lookup = dataProtector.lookup(value.getPassportNumber());
        if (passports.existsByDocumentNumberLookup(lookup)) return false;
        passports.save(new PassportDocumentJpaEntity(UUID.randomUUID().toString(), citizen.id(), lookup,
                payloadCodec.encrypt(passportPayload(value)), "ACTIVE", value.getDateOfIssue(), value.getDateOfExpiry(), 1, now));
        return true;
    }

    private boolean copyNationalIdentity(Citizen citizen, NationalIdentity value, Instant now) {
        String lookup = dataProtector.lookup(value.getNationalIdNumber());
        if (nationalIdentities.existsByDocumentNumberLookup(lookup)) return false;
        nationalIdentities.save(new NationalIdentityDocumentJpaEntity(UUID.randomUUID().toString(), citizen.id(), lookup,
                payloadCodec.encrypt(nationalIdentityPayload(value)), "ACTIVE", value.getDateOfIssue(), value.getDateOfExpiry(), 1, now));
        return true;
    }

    private boolean copyBirthCertificate(Citizen citizen, BirthCertificate value, Instant now) {
        String lookup = dataProtector.lookup(value.getCertificateNumber());
        if (birthCertificates.existsByDocumentNumberLookup(lookup)) return false;
        birthCertificates.save(new BirthCertificateDocumentJpaEntity(UUID.randomUUID().toString(), citizen.id(), lookup,
                payloadCodec.encrypt(birthCertificatePayload(value)), 1, now));
        return true;
    }

    private static Map<String, String> passportPayload(Passport v) {
        return payload("passportNumber", v.getPassportNumber(), "firstName", v.getName(), "lastName", v.getLastName(),
                "dateOfBirth", date(v.getDateOfBirth()), "placeOfBirth", v.getPlaceOfBirth(), "issuedOn", date(v.getDateOfIssue()),
                "expiresOn", date(v.getDateOfExpiry()), "placeOfIssue", v.getPlaceOfIssue(), "issuingAuthority", v.getIssuingAuthority(),
                "profession", v.getJob(), "nationality", v.getNationality(), "sex", v.getSex());
    }

    private static Map<String, String> nationalIdentityPayload(NationalIdentity v) {
        return payload("nationalId", v.getNationalIdNumber(), "firstName", v.getName(), "lastName", v.getLastName(),
                "gender", v.getGender(), "placeOfBirth", v.getPlaceOfBirth(), "dateOfBirth", date(v.getDateOfBirth()),
                "cardSerial", v.getCardSerial(), "placeOfIssue", v.getPlaceOfIssue(), "issuedOn", date(v.getDateOfIssue()),
                "expiresOn", date(v.getDateOfExpiry()), "profession", v.getProfession(), "fatherName", v.getFatherName(),
                "motherName", v.getMotherName(), "address", v.getAddress(), "bloodGroup", v.getBloodGroup());
    }

    private static Map<String, String> birthCertificatePayload(BirthCertificate v) {
        return payload("certificateNumber", v.getCertificateNumber(), "fullName", v.getFullName(), "gender", v.getGender(),
                "birthDate", date(v.getBirthDate()), "birthPlace", v.getBirthPlace(), "fatherName", v.getFatherName(),
                "fatherBirthDate", date(v.getFatherBirthDate()), "fatherBirthPlace", v.getFatherBirthPlace(),
                "fatherProfession", v.getFatherProfession(), "motherName", v.getMotherName(), "motherBirthDate", date(v.getMotherBirthDate()),
                "motherBirthPlace", v.getMotherBirthPlace(), "motherProfession", v.getMotherProfession(),
                "declarationDate", date(v.getDeclarationDate()), "address", v.getAddress());
    }

    private static Map<String, String> payload(String... entries) {
        Map<String, String> result = new LinkedHashMap<>();
        for (int index = 0; index < entries.length; index += 2) result.put(entries[index], entries[index + 1] == null ? "" : entries[index + 1]);
        return result;
    }

    private static String date(LocalDate value) { return value == null ? "" : value.toString(); }
    private static boolean isBlank(String value) { return value == null || value.isBlank(); }

    public record BackfillReport(int citizensVisited, int documentsCreated, int documentsSkipped) { }
}
