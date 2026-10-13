package com.nisum.bci.controller.advice;

import com.nisum.bci.constants.Constants;
import com.nisum.bci.controller.UserController;
import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.exception.EmailAlreadyExistsException;
import com.nisum.bci.exception.InvalidFieldException;
import com.nisum.bci.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final String REGISTER_URL = "/users/register";
    private static final String VALID_BODY = "{\"name\":\"Juan Rodriguez\",\"email\":\"juan@rodriguez.org\","
            + "\"password\":\"Hunter@2024\",\"phones\":[{\"number\":\"1234567\",\"citycode\":\"1\","
            + "\"countrycode\":\"57\"}]}";

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handler_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        when(userService.register(any(UserRegisterRequest.class))).thenThrow(new EmailAlreadyExistsException());

        performRegister(VALID_BODY)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_EMAIL_ALREADY_EXISTS));
        verify(userService, times(1)).register(any(UserRegisterRequest.class));
    }

    @Test
    void handler_shouldReturnConflict_whenDataIntegrityIsViolated() throws Exception {
        when(userService.register(any(UserRegisterRequest.class)))
                .thenThrow(new DataIntegrityViolationException("Unique index or primary key violation"));

        performRegister(VALID_BODY)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_EMAIL_ALREADY_EXISTS));
    }

    @Test
    void handler_shouldReturnBadRequest_whenFieldIsInvalid() throws Exception {
        when(userService.register(any(UserRegisterRequest.class)))
                .thenThrow(new InvalidFieldException(Constants.MSG_INVALID_EMAIL));

        performRegister(VALID_BODY)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_INVALID_EMAIL));
    }

    @Test
    void handler_shouldReturnBadRequestWithSortedMessages_whenRequiredFieldsAreMissing() throws Exception {
        String expected = String.join("; ", Constants.MSG_EMAIL_REQUIRED, Constants.MSG_NAME_REQUIRED,
                Constants.MSG_PASSWORD_REQUIRED);

        performRegister("{\"name\":\"\",\"email\":\"\",\"password\":\"\",\"phones\":[]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(expected));
        verify(userService, never()).register(any(UserRegisterRequest.class));
    }

    @Test
    void handler_shouldReturnBadRequest_whenBodyIsMalformed() throws Exception {
        performRegister("{\"name\":")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_MALFORMED_REQUEST));
    }

    @Test
    void handler_shouldReturnUnsupportedMediaType_whenContentTypeIsNotJson() throws Exception {
        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.TEXT_PLAIN).content(VALID_BODY))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_UNSUPPORTED_MEDIA_TYPE));
    }

    @Test
    void handler_shouldReturnMethodNotAllowed_whenMethodIsNotSupported() throws Exception {
        mockMvc.perform(get(REGISTER_URL))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_METHOD_NOT_ALLOWED));
    }

    @Test
    void handler_shouldReturnInternalServerError_whenUnexpectedExceptionOccurs() throws Exception {
        when(userService.register(any(UserRegisterRequest.class))).thenThrow(new IllegalStateException("boom"));

        performRegister(VALID_BODY)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_INTERNAL_ERROR));
    }

    private ResultActions performRegister(String body) throws Exception {
        return mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
