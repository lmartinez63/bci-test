package com.nisum.bci.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nisum.bci.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDTO {

    @NotBlank(message = Constants.MSG_PHONE_NUMBER_REQUIRED)
    private String number;

    @JsonProperty("citycode")
    private String cityCode;

    @JsonProperty("countrycode")
    private String countryCode;
}
