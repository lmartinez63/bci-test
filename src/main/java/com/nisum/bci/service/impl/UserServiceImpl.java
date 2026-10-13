package com.nisum.bci.service.impl;

import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.dto.UserRegisterResponse;
import com.nisum.bci.entity.User;
import com.nisum.bci.mapper.UserMapper;
import com.nisum.bci.repository.UserRepository;
import com.nisum.bci.service.JwtService;
import com.nisum.bci.service.PasswordService;
import com.nisum.bci.service.UserService;
import com.nisum.bci.util.MaskingUtils;
import com.nisum.bci.validator.ValidateUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    /**
     * Registers a new user; the request is validated by UserValidationAspect through @ValidateUser
     * @param request - UserRegisterRequest
     * @return - UserRegisterResponse
     */
    @Override
    @ValidateUser
    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request) {
        String maskedEmail = MaskingUtils.maskEmail(request.getEmail());
        LocalDateTime now = LocalDateTime.now();
        User user = userMapper.toEntity(request);
        user.setPassword(passwordService.encode(request.getPassword()));
        user.setToken(jwtService.generateToken(user.getEmail()));
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);
        user.setIsActive(Boolean.TRUE);

        log.debug("action=registerUser, step=persist, email={}, phones={}", maskedEmail, user.getPhones().size());
        User savedUser = userRepository.saveAndFlush(user);
        log.info("action=registerUser, status=persisted, userId={}, email={}", savedUser.getId(), maskedEmail);
        return userMapper.toResponse(savedUser);
    }
}
