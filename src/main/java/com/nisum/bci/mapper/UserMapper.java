package com.nisum.bci.mapper;

import com.nisum.bci.dto.PhoneDTO;
import com.nisum.bci.dto.UserRegisterRequest;
import com.nisum.bci.dto.UserRegisterResponse;
import com.nisum.bci.entity.Phone;
import com.nisum.bci.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserMapper implements EntityMapper<UserRegisterRequest, User> {

    /**
     * Maps the registration request to a User entity (password, token and dates are set by the service)
     * @param request - UserRegisterRequest
     * @return - User
     */
    @Override
    public User toEntity(UserRegisterRequest request) {
        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase(Locale.ROOT))
                .build();
        Optional.ofNullable(request.getPhones())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toPhone)
                .forEach(user::addPhone);
        return user;
    }

    /**
     * Maps the persisted User to the registration response
     * @param user - User
     * @return - UserRegisterResponse
     */
    public UserRegisterResponse toResponse(User user) {
        return UserRegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phones(user.getPhones().stream()
                        .map(this::toPhoneDto)
                        .collect(Collectors.toList()))
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .isActive(user.getIsActive())
                .build();
    }

    private Phone toPhone(PhoneDTO phoneDto) {
        return Phone.builder()
                .number(phoneDto.getNumber())
                .cityCode(phoneDto.getCityCode())
                .countryCode(phoneDto.getCountryCode())
                .build();
    }

    private PhoneDTO toPhoneDto(Phone phone) {
        return PhoneDTO.builder()
                .number(phone.getNumber())
                .cityCode(phone.getCityCode())
                .countryCode(phone.getCountryCode())
                .build();
    }
}
