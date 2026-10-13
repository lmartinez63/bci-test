package com.nisum.bci.validator;

import com.nisum.bci.dto.UserRegisterRequest;

/**
 * Validation rule applied to a user registration request.
 * Implementations are executed in the order defined by {@link org.springframework.core.annotation.Order}.
 */
public interface UserValidator {

    void validate(UserRegisterRequest request);
}
