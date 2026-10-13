package com.nisum.bci.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.nisum.bci.config.JwtProperties;
import com.nisum.bci.constants.Constants;
import com.nisum.bci.service.JwtService;
import com.nisum.bci.util.MaskingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        this.verifier = JWT.require(algorithm).build();
        this.expirationMs = jwtProperties.getExpirationMs();
    }

    /**
     * Generates a HS256 signed token with the email as subject and claim
     * @param email - user email
     * @return - signed JWT
     */
    @Override
    public String generateToken(String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMs);
        log.debug("action=generateToken, algorithm={}, email={}, expiresAt={}",
                algorithm.getName(), MaskingUtils.maskEmail(email), expiresAt);
        return JWT.create()
                .withSubject(email)
                .withClaim(Constants.EMAIL_CLAIM, email)
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    /**
     * Verifies signature and expiration and returns the email claim
     * @param token - JWT
     * @return - email claim
     */
    @Override
    public String extractEmail(String token) {
        try {
            return verifier.verify(token).getClaim(Constants.EMAIL_CLAIM).asString();
        } catch (JWTVerificationException e) {
            log.warn("action=verifyToken, status=invalid, reason={}", e.getMessage());
            throw e;
        }
    }
}
