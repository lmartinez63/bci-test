package com.nisum.bci.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PasswordAlgorithm {
    SHA512("pbkdf2-sha512"),
    BCRYPT("bcrypt");

    private final String encoderId;
}
