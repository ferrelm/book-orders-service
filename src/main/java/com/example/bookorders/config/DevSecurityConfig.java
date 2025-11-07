package com.example.bookorders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

/**
 * Development security configuration.
 *
 * Activates only when the 'dev' profile is active. It permits unauthenticated access
 * to actuator endpoints (/actuator/**) while leaving other endpoints protected.
 */
@Configuration
@Profile("dev")
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // In 'dev' profile we allow unauthenticated access to actuator endpoints
        // while requiring authentication for all other endpoints. Keep the
        // oauth2 resource-server configuration for non-actuator requests.
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
