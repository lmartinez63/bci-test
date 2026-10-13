package com.nisum.bci.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiSwaggerConfig {

    private static final String TITLE = "BCI User Registration API";
    private static final String DESCRIPTION = "User registration API with JWT token generation";
    private static final String VERSION = "1.0.0";
    private static final String CONTACT_NAME = "Luis Fernando Martinez";
    private static final String CONTACT_EMAIL = "l.martinez.rod@gmail.com";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(this.getInfo());
    }

    private Info getInfo() {
        return new Info()
                .title(TITLE)
                .description(DESCRIPTION)
                .version(VERSION)
                .contact(this.getContact());
    }

    private Contact getContact() {
        return new Contact()
                .name(CONTACT_NAME)
                .email(CONTACT_EMAIL);
    }
}
