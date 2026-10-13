package com.nisum.bci.service;

import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.dto.UserRegisterResponse;

public interface UserService {

    UserRegisterResponse register(UserRegisterRequest request);
}
