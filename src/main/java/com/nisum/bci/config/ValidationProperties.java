package com.nisum.bci.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "validation")
public class ValidationProperties {

    private String emailRegex;
    private String passwordRegex;
}
