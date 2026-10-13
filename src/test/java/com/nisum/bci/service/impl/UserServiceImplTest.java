package com.nisum.bci.service.impl;

import com.nisum.bci.dto.PhoneDTO;
import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.dto.UserRegisterResponse;
import com.nisum.bci.entity.User;
import com.nisum.bci.exception.EmailAlreadyExistsException;
import com.nisum.bci.exception.InvalidFieldException;
import com.nisum.bci.mapper.UserMapper;
import com.nisum.bci.repository.UserRepository;
import com.nisum.bci.service.JwtService;
import com.nisum.bci.service.PasswordService;
import com.nisum.bci.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String EMAIL = "juan@rodriguez.org";
    private static final String PASSWORD = "Hunter@2024";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded";
    private static final String TOKEN = "jwt-token";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator emailValidator;

    @Mock
    private UserValidator passwordValidator;

    @Mock
    private PasswordService passwordService;

    @Mock
    private JwtService jwtService;

    private UserServiceImpl userService;

    private UserRegisterRequest request;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, Arrays.asList(emailValidator, passwordValidator),
                new UserMapper(), passwordService, jwtService);
        request = UserRegisterRequest.builder()
                .name("Juan Rodriguez")
                .email(EMAIL)
                .password(PASSWORD)
                .phones(Collections.singletonList(PhoneDTO.builder()
                        .number("1234567")
                        .cityCode("1")
                        .countryCode("57")
                        .build()))
                .build();
    }

    @Test
    void register_shouldReturnResponseWithToken_whenRequestIsValid() {
        UUID userId = UUID.randomUUID();
        when(passwordService.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(EMAIL)).thenReturn(TOKEN);
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(userId);
            return user;
        });

        UserRegisterResponse response = userService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).saveAndFlush(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
        assertEquals(1, savedUser.getPhones().size());
        assertSame(savedUser, savedUser.getPhones().get(0).getUser());

        assertEquals(userId, response.getId());
        assertEquals(EMAIL, response.getEmail());
        assertEquals(TOKEN, response.getToken());
        assertTrue(response.getIsActive());
        assertEquals(response.getCreated(), response.getModified());
        assertEquals(response.getCreated(), response.getLastLogin());
        assertEquals("1234567", response.getPhones().get(0).getNumber());
    }

    @Test
    void register_shouldStoreEmailInLowerCase() {
        request.setEmail("Juan@Rodriguez.ORG");
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRegisterResponse response = userService.register(request);

        assertEquals(EMAIL, response.getEmail());
        verify(jwtService, times(1)).generateToken(EMAIL);
    }

    @Test
    void register_shouldThrowEmailAlreadyExistsException_whenEmailIsRegistered() {
        doThrow(new EmailAlreadyExistsException()).when(emailValidator).validate(request);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(request));

        verify(passwordValidator, never()).validate(any());
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void register_shouldThrowInvalidFieldException_whenPasswordIsInvalid() {
        doThrow(new InvalidFieldException("invalid password")).when(passwordValidator).validate(request);

        assertThrows(InvalidFieldException.class, () -> userService.register(request));

        verify(emailValidator, times(1)).validate(request);
        verify(passwordService, never()).encode(any());
        verify(userRepository, never()).saveAndFlush(any());
    }
}
