package com.absher.absherapp.shared.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonalDataProtectionConfiguration {

    @Bean
    public PersonalDataProtector personalDataProtector(
            @Value("${app.security.lookup-key-base64}") String lookupKeyBase64,
            @Value("${app.security.encryption-key-base64}") String encryptionKeyBase64
    ) {
        return new AesGcmPersonalDataProtector(lookupKeyBase64, encryptionKeyBase64);
    }
}
