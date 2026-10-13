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

class PasswordFormatValidatorTest {

    private PasswordFormatValidator passwordFormatValidator;

    @BeforeEach
    void setUp() {
        ValidationProperties validationProperties = new ValidationProperties();
        validationProperties.setPasswordRegex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$");
        passwordFormatValidator = new PasswordFormatValidator(validationProperties);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hunter@2024", "Abcdef1#", "PASSWORD9+"})
    void validate_shouldPass_whenPasswordIsValid(String password) {
        assertDoesNotThrow(() -> passwordFormatValidator.validate(
                UserRegisterRequest.builder().password(password).build()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"hunter@2024", "Hunter@abc", "Hunter2024", "Hu@2024"})
    void validate_shouldThrowInvalidFieldException_whenPasswordIsInvalid(String password) {
        UserRegisterRequest request = UserRegisterRequest.builder().password(password).build();

        InvalidFieldException exception = assertThrows(InvalidFieldException.class,
                () -> passwordFormatValidator.validate(request));

        assertEquals(Constants.MSG_INVALID_PASSWORD, exception.getMessage());
    }
}
