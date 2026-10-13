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
@Order(1)
@Component
public class EmailFormatValidator implements UserValidator {

    private final Pattern emailPattern;

    public EmailFormatValidator(ValidationProperties validationProperties) {
        this.emailPattern = Pattern.compile(validationProperties.getEmailRegex());
    }

    /**
     * Validates the email against the configured regex
     * @param request - UserRegisterRequest
     */
    @Override
    public void validate(UserRegisterRequest request) {
        if (!emailPattern.matcher(request.getEmail()).matches()) {
            log.warn("action=validateUser, validator=EmailFormatValidator, result=rejected, email={}",
                    MaskingUtils.maskEmail(request.getEmail()));
            throw new InvalidFieldException(Constants.MSG_INVALID_EMAIL);
        }
    }
}
