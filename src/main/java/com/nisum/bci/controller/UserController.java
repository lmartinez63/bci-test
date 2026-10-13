package com.nisum.bci.controller;

import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.dto.UserRegisterResponse;
import com.nisum.bci.service.UserService;
import com.nisum.bci.util.MaskingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** Register a new user
     * @param request - UserRegisterRequest
     * @return - UserRegisterResponse with the generated token
     */
    @Operation(description = "Registers a new user and returns the JWT token")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "409", description = "Conflict"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")})
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        int phones = request.getPhones() == null ? 0 : request.getPhones().size();
        log.info("action=registerUser, status=received, email={}, phones={}",
                MaskingUtils.maskEmail(request.getEmail()), phones);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }
}
