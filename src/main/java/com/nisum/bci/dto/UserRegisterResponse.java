package com.nisum.bci.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterResponse {

    private UUID id;

    private String name;

    private String email;

    private List<PhoneDTO> phones;

    private LocalDateTime created;

    private LocalDateTime modified;

    @JsonProperty("last_login")
    private LocalDateTime lastLogin;

    private String token;

    @JsonProperty("isactive")
    private Boolean isActive;
}
