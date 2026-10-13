package com.nisum.bci.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nisum.bci.constants.Constants;
import com.nisum.bci.entity.User;
import com.nisum.bci.repository.UserRepository;
import com.nisum.bci.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String REGISTER_URL = "/users/register";
    private static final String VALID_PASSWORD = "Hunter@2024";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void register_shouldReturnCreated_whenRequestIsValid() throws Exception {
        String email = randomEmail();

        String body = performRegister(buildRequest(email, VALID_PASSWORD, "1234567"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.isactive").value(true))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.modified").isNotEmpty())
                .andExpect(jsonPath("$.last_login").isNotEmpty())
                .andExpect(jsonPath("$.phones[0].number").value("1234567"))
                .andExpect(jsonPath("$.phones[0].citycode").value("1"))
                .andExpect(jsonPath("$.phones[0].countrycode").value("57"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        JsonNode response = objectMapper.readTree(body);
        assertDoesNotThrow(() -> UUID.fromString(response.get("id").asText()));
        assertEquals(email, jwtService.extractEmail(response.get("token").asText()));
    }

    @Test
    void register_shouldStorePasswordHashedWithBcrypt() throws Exception {
        String email = randomEmail();
        performRegister(buildRequest(email, VALID_PASSWORD, "1234567")).andExpect(status().isCreated());

        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow();
        assertTrue(user.getPassword().startsWith("{bcrypt}"));
    }

    @Test
    void register_shouldReturnRequestIdHeader() throws Exception {
        ObjectNode request = buildRequest(randomEmail(), VALID_PASSWORD, "1234567");

        mockMvc.perform(post(REGISTER_URL)
                        .header(Constants.REQUEST_ID_HEADER, "test-request-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(Constants.REQUEST_ID_HEADER, "test-request-id"));
    }

    @Test
    void register_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        String email = randomEmail();
        performRegister(buildRequest(email, VALID_PASSWORD, "1234567")).andExpect(status().isCreated());

        performRegister(buildRequest(email.toUpperCase(), VALID_PASSWORD, "7654321"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_EMAIL_ALREADY_EXISTS));
    }

    @Test
    void register_shouldReturnBadRequest_whenPasswordIsInvalid() throws Exception {
        performRegister(buildRequest(randomEmail(), "hunter2024", "1234567"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_INVALID_PASSWORD));
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        performRegister(buildRequest("juan.rodriguez.org", VALID_PASSWORD, "1234567"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_INVALID_EMAIL));
    }

    @Test
    void register_shouldReturnBadRequest_whenPhoneNumberIsMissing() throws Exception {
        performRegister(buildRequest(randomEmail(), VALID_PASSWORD, null))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_PHONE_NUMBER_REQUIRED));
    }

    @Test
    void register_shouldReturnBadRequest_whenPhoneIsNull() throws Exception {
        ObjectNode request = buildRequest(randomEmail(), VALID_PASSWORD, "1234567");
        request.putArray("phones").addNull();

        performRegister(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_PHONE_REQUIRED));
    }

    @Test
    void register_shouldReturnBadRequest_whenBodyIsMalformed() throws Exception {
        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content("{name"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(Constants.MSG_MALFORMED_REQUEST));
    }

    private ResultActions performRegister(ObjectNode request) throws Exception {
        return mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private ObjectNode buildRequest(String email, String password, String phoneNumber) {
        ObjectNode request = objectMapper.createObjectNode()
                .put("name", "Juan Rodriguez")
                .put("email", email)
                .put("password", password);
        request.putArray("phones").addObject()
                .put("number", phoneNumber)
                .put("citycode", "1")
                .put("countrycode", "57");
        return request;
    }

    private String randomEmail() {
        return "user" + UUID.randomUUID().toString().substring(0, 8) + "@rodriguez.org";
    }
}
