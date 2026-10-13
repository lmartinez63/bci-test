package com.nisum.bci.validator;

import com.nisum.bci.config.ValidationProperties;
import com.nisum.bci.constants.Constants;
import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.exception.InvalidFieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailFormatValidatorTest {

    private EmailFormatValidator emailFormatValidator;

    @BeforeEach
    void setUp() {
        ValidationProperties validationProperties = new ValidationProperties();
        validationProperties.setEmailRegex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        emailFormatValidator = new EmailFormatValidator(validationProperties);
    }

    @ParameterizedTest
    @ValueSource(strings = {"juan@rodriguez.org", "juan.perez+test@bci.cl", "JUAN@DOMAIN.COM"})
    void validate_shouldPass_whenEmailIsValid(String email) {
        assertDoesNotThrow(() -> emailFormatValidator.validate(UserRegisterRequest.builder().email(email).build()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"juan.rodriguez.org", "juan@", "@rodriguez.org", "juan@rodriguez", "juan @rodriguez.org"})
    void validate_shouldThrowInvalidFieldException_whenEmailIsInvalid(String email) {
        UserRegisterRequest request = UserRegisterRequest.builder().email(email).build();

        InvalidFieldException exception = assertThrows(InvalidFieldException.class,
                () -> emailFormatValidator.validate(request));

        assertEquals(Constants.MSG_INVALID_EMAIL, exception.getMessage());
    }
}
