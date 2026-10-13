package com.nisum.bci.service.impl;

import com.nisum.bci.config.PasswordProperties;
import com.nisum.bci.config.SecurityConfig;
import com.nisum.bci.constants.PasswordAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordServiceImplTest {

    private static final String PASSWORD = "Hunter@2024";
    private static final String SHA512_PREFIX = "{pbkdf2-sha512}";

    @Test
    void encode_shouldUseSha512_whenAlgorithmIsSha512() {
        PasswordServiceImpl passwordService = buildService(PasswordAlgorithm.SHA512);

        String hash = passwordService.encode(PASSWORD);

        assertTrue(hash.startsWith(SHA512_PREFIX));
        assertTrue(passwordService.matches(PASSWORD, hash));
        assertFalse(passwordService.matches("Hunter@2025", hash));
    }

    @Test
    void encode_shouldGenerateDifferentHashes_forSamePassword() {
        PasswordServiceImpl passwordService = buildService(PasswordAlgorithm.SHA512);

        assertNotEquals(passwordService.encode(PASSWORD), passwordService.encode(PASSWORD));
    }

    @Test
    void encode_shouldUseBcrypt_whenAlgorithmIsBcrypt() {
        PasswordServiceImpl passwordService = buildService(PasswordAlgorithm.BCRYPT);

        String hash = passwordService.encode(PASSWORD);

        assertTrue(hash.startsWith("{bcrypt}"));
        assertTrue(passwordService.matches(PASSWORD, hash));
    }

    @Test
    void matches_shouldVerifyBcryptHash_whenAlgorithmIsSha512() {
        PasswordServiceImpl passwordService = buildService(PasswordAlgorithm.SHA512);
        String bcryptHash = "{bcrypt}" + new BCryptPasswordEncoder().encode(PASSWORD);

        assertTrue(passwordService.matches(PASSWORD, bcryptHash));
    }

    private PasswordServiceImpl buildService(PasswordAlgorithm algorithm) {
        PasswordProperties passwordProperties = new PasswordProperties();
        passwordProperties.setAlgorithm(algorithm);
        passwordProperties.setIterations(1_000);
        return new PasswordServiceImpl(new SecurityConfig().passwordEncoder(passwordProperties), passwordProperties);
    }
}
