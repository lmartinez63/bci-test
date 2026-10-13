package com.nisum.bci.service.impl;

import com.nisum.bci.config.PasswordProperties;
import com.nisum.bci.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final PasswordProperties passwordProperties;

    /**
     * Hashes the password with the configured algorithm (BCRYPT by default)
     * @param raw - plain password
     * @return - hash prefixed with the algorithm id
     */
    @Override
    public String encode(String raw) {
        log.debug("action=encodePassword, algorithm={}", passwordProperties.getAlgorithm());
        return passwordEncoder.encode(raw);
    }

    @Override
    public boolean matches(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }
}
