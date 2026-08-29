package com.chari.chariapp.birthrequest.infrastructure.persistence;

import com.chari.chariapp.birthrequest.domain.NewbornGender;
import com.chari.chariapp.birthrequest.domain.NewbornRegistrationDetails;
import com.chari.chariapp.shared.security.PersonalDataProtector;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Encrypts the entire newborn payload so personal fields never appear as database columns. */
@Component
public class NewbornRegistrationDetailsCipher {

    private final PersonalDataProtector dataProtector;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NewbornRegistrationDetailsCipher(PersonalDataProtector dataProtector) {
        this.dataProtector = dataProtector;
    }

    public String encrypt(NewbornRegistrationDetails details) {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("childFirstName", details.childFirstName());
        payload.put("childLastName", details.childLastName());
        payload.put("dateOfBirth", details.dateOfBirth().toString());
        payload.put("placeOfBirth", details.placeOfBirth());
        payload.put("gender", details.gender().name());
        payload.put("fatherFullName", details.fatherFullName());
        payload.put("fatherNationalId", details.fatherNationalId());
        payload.put("motherFullName", details.motherFullName());
        payload.put("motherNationalId", details.motherNationalId());
        try {
            return dataProtector.encrypt(objectMapper.writeValueAsString(payload));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to protect newborn registration details", exception);
        }
    }

    public NewbornRegistrationDetails decrypt(String encryptedPayload) {
        try {
            Map<String, String> payload = objectMapper.readValue(dataProtector.decrypt(encryptedPayload), new TypeReference<>() { });
            return new NewbornRegistrationDetails(
                    value(payload, "childFirstName"), value(payload, "childLastName"),
                    LocalDate.parse(value(payload, "dateOfBirth")), value(payload, "placeOfBirth"),
                    NewbornGender.valueOf(value(payload, "gender")), value(payload, "fatherFullName"),
                    value(payload, "fatherNationalId"), value(payload, "motherFullName"), value(payload, "motherNationalId")
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to read protected newborn registration details", exception);
        }
    }

    private String value(Map<String, String> payload, String key) {
        String value = payload.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing protected newborn field: " + key);
        }
        return value;
    }
}
