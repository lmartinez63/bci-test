package com.nisum.bci.validator;

import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.exception.EmailAlreadyExistsException;
import com.nisum.bci.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailAlreadyRegisteredValidatorTest {

    private static final String EMAIL = "juan@rodriguez.org";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailAlreadyRegisteredValidator emailAlreadyRegisteredValidator;

    @Test
    void validate_shouldThrowEmailAlreadyExistsException_whenEmailExists() {
        UserRegisterRequest request = UserRegisterRequest.builder().email(EMAIL).build();
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> emailAlreadyRegisteredValidator.validate(request));
        verify(userRepository, times(1)).existsByEmailIgnoreCase(EMAIL);
    }

    @Test
    void validate_shouldPass_whenEmailDoesNotExist() {
        UserRegisterRequest request = UserRegisterRequest.builder().email(EMAIL).build();
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);

        assertDoesNotThrow(() -> emailAlreadyRegisteredValidator.validate(request));
    }
}
