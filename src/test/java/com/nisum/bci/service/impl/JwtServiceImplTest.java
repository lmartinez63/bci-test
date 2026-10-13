package com.nisum.bci.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nisum.bci.config.JwtProperties;
import com.nisum.bci.constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceImplTest {

    private static final String EMAIL = "juan@rodriguez.org";
    private static final long ONE_DAY_MS = 86_400_000L;

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(buildProperties("test-secret-0123456789-0123456789"));
    }

    @Test
    void generateToken_shouldContainEmailClaimAndExpireInOneDay() {
        String token = jwtService.generateToken(EMAIL);

        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals("HS256", decodedJWT.getAlgorithm());
        assertEquals(EMAIL, decodedJWT.getClaim(Constants.EMAIL_CLAIM).asString());
        assertEquals(Duration.ofMillis(ONE_DAY_MS),
                Duration.between(decodedJWT.getIssuedAtAsInstant(), decodedJWT.getExpiresAtAsInstant()));
    }

    @Test
    void extractEmail_shouldReturnEmail_whenTokenIsValid() {
        String token = jwtService.generateToken(EMAIL);

        assertEquals(EMAIL, jwtService.extractEmail(token));
    }

    @Test
    void extractEmail_shouldThrowException_whenTokenIsSignedWithAnotherSecret() {
        String token = new JwtServiceImpl(buildProperties("another-secret-0123456789-012345")).generateToken(EMAIL);

        assertThrows(JWTVerificationException.class, () -> jwtService.extractEmail(token));
    }

    private JwtProperties buildProperties(String secret) {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret(secret);
        jwtProperties.setExpirationMs(ONE_DAY_MS);
        return jwtProperties;
    }
}
