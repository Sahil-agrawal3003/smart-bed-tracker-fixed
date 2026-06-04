package com.hospital.smartbedtracker.config;

import com.hospital.smartbedtracker.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService) {

        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                customUserDetailsService);

        provider.setPasswordEncoder(
                passwordEncoder());

        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        http
                .authenticationProvider(
                        authenticationProvider())

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/h2-console/**",
                                "/ws/**",
                                "/api/beds/stats",
                                "/api/auth/register"
                        ).permitAll()

                        .requestMatchers("/api/auth/me")
                        .authenticated()

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/beds/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/beds/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/beds/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/beds/**")
                        .hasAnyRole("ADMIN", "USER")

                        .requestMatchers("/api/patients/**")
                        .hasAnyRole("ADMIN", "USER")

                        .requestMatchers("/api/wards/**")
                        .hasAnyRole("ADMIN", "USER")

                        .anyRequest()
                        .authenticated()
                )

                .httpBasic(Customizer.withDefaults())

                .headers(headers ->
                        headers.frameOptions(
                                frame -> frame.disable()
                        ));

        return http.build();
    }


}
