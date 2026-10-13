package com.nisum.bci.dto;

import com.nisum.bci.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class UserRegisterRequest {

    @NotBlank(message = Constants.MSG_NAME_REQUIRED)
    private String name;

    @NotBlank(message = Constants.MSG_EMAIL_REQUIRED)
    private String email;

    @NotBlank(message = Constants.MSG_PASSWORD_REQUIRED)
    private String password;

    @Builder.Default
    private List<@NotNull(message = Constants.MSG_PHONE_REQUIRED) @Valid PhoneDTO> phones = new ArrayList<>();
}
