package com.nisum.bci.config;

import com.nisum.bci.constants.PasswordAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "password")
public class PasswordProperties {

    private PasswordAlgorithm algorithm = PasswordAlgorithm.BCRYPT;
    private int iterations = 210_000;
    private int saltLength = 16;
}
