package com.chari.chariapp.shared.security;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;

public final class AesGcmPersonalDataProtector implements PersonalDataProtector {

    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_IV_BYTES = 12;

    private final byte[] lookupKey;
    private final SecretKeySpec encryptionKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesGcmPersonalDataProtector(String lookupKeyBase64, String encryptionKeyBase64) {
        lookupKey = decodeKey(lookupKeyBase64, "lookup");
        byte[] encryptionKeyBytes = decodeKey(encryptionKeyBase64, "encryption");
        if (encryptionKeyBytes.length != 16 && encryptionKeyBytes.length != 24 && encryptionKeyBytes.length != 32) {
            throw new IllegalArgumentException("Encryption key must be 128, 192, or 256 bits");
        }
        encryptionKey = new SecretKeySpec(encryptionKeyBytes, "AES");
    }

    @Override
    public String lookup(String normalizedValue) {
        Objects.requireNonNull(normalizedValue, "Value is required");
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(lookupKey, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(normalizedValue.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Unable to create protected lookup", ex);
        }
    }

    @Override
    public String encrypt(String plaintext) {
        Objects.requireNonNull(plaintext, "Value is required");
        try {
            byte[] iv = new byte[GCM_IV_BYTES];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(encrypted, 0, payload, iv.length, encrypted.length);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(payload);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Unable to encrypt personal data", ex);
        }
    }

    @Override
    public String decrypt(String ciphertext) {
        Objects.requireNonNull(ciphertext, "Ciphertext is required");
        try {
            byte[] payload = Base64.getUrlDecoder().decode(ciphertext);
            if (payload.length <= GCM_IV_BYTES) {
                throw new IllegalArgumentException("Invalid ciphertext payload");
            }
            byte[] iv = java.util.Arrays.copyOfRange(payload, 0, GCM_IV_BYTES);
            byte[] encrypted = java.util.Arrays.copyOfRange(payload, GCM_IV_BYTES, payload.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unable to decrypt protected data", ex);
        }
    }

    private static byte[] decodeKey(String encodedKey, String keyName) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encodedKey);
            if (decoded.length < 32) {
                throw new IllegalArgumentException(keyName + " key must be at least 256 bits");
            }
            return decoded;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid " + keyName + " key configuration", ex);
        }
    }
}
