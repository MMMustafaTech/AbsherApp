package com.chari.chariapp.document.infrastructure.persistence;

import com.chari.chariapp.shared.security.PersonalDataProtector;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EncryptedDocumentPayloadCodec {
    private final PersonalDataProtector dataProtector;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EncryptedDocumentPayloadCodec(PersonalDataProtector dataProtector) {
        this.dataProtector = dataProtector;
    }

    public String encrypt(Map<String, String> payload) {
        try {
            return dataProtector.encrypt(objectMapper.writeValueAsString(payload));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to serialize document payload", ex);
        }
    }

    public Map<String, String> decrypt(String encryptedPayload) {
        try {
            return objectMapper.readValue(dataProtector.decrypt(encryptedPayload), new TypeReference<>() { });
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to read document payload", ex);
        }
    }
}
