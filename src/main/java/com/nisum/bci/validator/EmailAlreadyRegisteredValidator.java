package com.nisum.bci.validator;

import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.exception.EmailAlreadyExistsException;
import com.nisum.bci.repository.UserRepository;
import com.nisum.bci.util.MaskingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(3)
@Component
@RequiredArgsConstructor
public class EmailAlreadyRegisteredValidator implements UserValidator {

    private final UserRepository userRepository;

    /**
     * Validates that the email is not registered yet
     * @param request - UserRegisterRequest
     */
    @Override
    public void validate(UserRegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            log.warn("action=validateUser, validator=EmailAlreadyRegisteredValidator, result=rejected, email={}",
                    MaskingUtils.maskEmail(request.getEmail()));
            throw new EmailAlreadyExistsException();
        }
    }
}
