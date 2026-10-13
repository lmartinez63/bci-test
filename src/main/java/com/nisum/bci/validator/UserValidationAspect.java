package com.nisum.bci.validator;

import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.util.MaskingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@RequiredArgsConstructor
public class UserValidationAspect {

    private final List<UserValidator> userValidators;

    /**
     * Decorates the methods annotated with {@link ValidateUser}: runs the validators in their
     * {@link Order} before the decorated method, so an invalid request never opens a transaction
     * @param request - UserRegisterRequest
     */
    @Before("@annotation(com.nisum.bci.validator.ValidateUser) && args(request,..)")
    public void validate(UserRegisterRequest request) {
        log.debug("action=validateUser, validators={}, email={}", userValidators.size(),
                MaskingUtils.maskEmail(request.getEmail()));
        userValidators.forEach(validator -> validator.validate(request));
    }
}
