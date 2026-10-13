package com.nisum.bci.validator;

import com.nisum.bci.config.ValidationProperties;
import com.nisum.bci.constants.Constants;
import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.exception.InvalidFieldException;
import com.nisum.bci.util.MaskingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Order(2)
@Component
public class PasswordFormatValidator implements UserValidator {

    private final Pattern passwordPattern;

    public PasswordFormatValidator(ValidationProperties validationProperties) {
        this.passwordPattern = Pattern.compile(validationProperties.getPasswordRegex());
    }

    /**
     * Validates the password against the configured regex
     * @param request - UserRegisterRequest
     */
    @Override
    public void validate(UserRegisterRequest request) {
        if (!passwordPattern.matcher(request.getPassword()).matches()) {
            log.warn("action=validateUser, validator=PasswordFormatValidator, result=rejected, email={}",
                    MaskingUtils.maskEmail(request.getEmail()));
            throw new InvalidFieldException(Constants.MSG_INVALID_PASSWORD);
        }
    }
}
