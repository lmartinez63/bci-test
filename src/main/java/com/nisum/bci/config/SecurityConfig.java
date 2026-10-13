package com.nisum.bci.config;

import com.nisum.bci.constants.PasswordAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class SecurityConfig {

    private static final int SHA512_HASH_WIDTH = 512;
    private static final int BCRYPT_STRENGTH = 10;
    private static final String[] PUBLIC_PATHS = {
            "/users/register",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            "/error"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(PUBLIC_PATHS).permitAll()
                .anyRequest().authenticated();
        return http.build();
    }

    /**
     * Password encoder that hashes with the configured algorithm and keeps the algorithm id
     * as prefix of the stored hash, so hashes of any registered algorithm can be verified
     * @param passwordProperties - PasswordProperties
     * @return - PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder(PasswordProperties passwordProperties) {
        Pbkdf2PasswordEncoder sha512Encoder = new Pbkdf2PasswordEncoder("",
                passwordProperties.getSaltLength(), passwordProperties.getIterations(), SHA512_HASH_WIDTH);
        sha512Encoder.setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(PasswordAlgorithm.SHA512.getEncoderId(), sha512Encoder);
        encoders.put(PasswordAlgorithm.BCRYPT.getEncoderId(), new BCryptPasswordEncoder(BCRYPT_STRENGTH));

        PasswordAlgorithm algorithm = passwordProperties.getAlgorithm();
        if (algorithm == PasswordAlgorithm.SHA512) {
            log.info("action=configurePasswordEncoder, algorithm={}, iterations={}, saltLength={}",
                    algorithm, passwordProperties.getIterations(), passwordProperties.getSaltLength());
        } else {
            log.info("action=configurePasswordEncoder, algorithm={}, strength={}", algorithm, BCRYPT_STRENGTH);
        }
        return new DelegatingPasswordEncoder(algorithm.getEncoderId(), encoders);
    }
}
