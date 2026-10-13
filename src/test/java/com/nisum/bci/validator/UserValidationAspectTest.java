package com.nisum.bci.validator;

import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.dto.UserRegisterResponse;
import com.nisum.bci.exception.EmailAlreadyExistsException;
import com.nisum.bci.exception.InvalidFieldException;
import com.nisum.bci.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserValidationAspectTest {

    @Mock
    private UserValidator emailValidator;

    @Mock
    private UserValidator passwordValidator;

    private RecordingUserService target;

    private UserService decoratedService;

    private UserRegisterRequest request;

    @BeforeEach
    void setUp() {
        target = new RecordingUserService();
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(target);
        proxyFactory.addAspect(new UserValidationAspect(Arrays.asList(emailValidator, passwordValidator)));
        decoratedService = proxyFactory.getProxy();
        request = UserRegisterRequest.builder()
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("Hunter@2024")
                .build();
    }

    @Test
    void validate_shouldRunValidatorsInOrderAndThenDelegate_whenRequestIsValid() {
        UserRegisterResponse response = decoratedService.register(request);

        InOrder order = inOrder(emailValidator, passwordValidator);
        order.verify(emailValidator, times(1)).validate(request);
        order.verify(passwordValidator, times(1)).validate(request);
        assertSame(target.response, response);
        assertEquals(1, target.calls);
    }

    @Test
    void validate_shouldStopChainAndNotDelegate_whenEmailIsRegistered() {
        doThrow(new EmailAlreadyExistsException()).when(emailValidator).validate(request);

        assertThrows(EmailAlreadyExistsException.class, () -> decoratedService.register(request));

        verify(passwordValidator, never()).validate(any());
        assertEquals(0, target.calls);
    }

    @Test
    void validate_shouldNotDelegate_whenPasswordIsInvalid() {
        doThrow(new InvalidFieldException("invalid password")).when(passwordValidator).validate(request);

        assertThrows(InvalidFieldException.class, () -> decoratedService.register(request));

        verify(emailValidator, times(1)).validate(request);
        assertEquals(0, target.calls);
    }

    private static class RecordingUserService implements UserService {

        private final UserRegisterResponse response = new UserRegisterResponse();
        private int calls;

        @Override
        @ValidateUser
        public UserRegisterResponse register(UserRegisterRequest request) {
            calls++;
            return response;
        }
    }
}
