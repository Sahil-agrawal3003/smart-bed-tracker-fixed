package com.hospital.smartbedtracker.service;

import com.hospital.smartbedtracker.entity.AppUser;
import com.hospital.smartbedtracker.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String email)
            throws UsernameNotFoundException {

        System.out.println(
                "LOGIN ATTEMPT = " + email);

        AppUser user =
                userRepository.findByEmail(email)
                        .orElseThrow(() -> {

                            System.out.println(
                                    "USER NOT FOUND");

                            return new UsernameNotFoundException(
                                    "User not found");
                        });

        System.out.println(
                "USER FOUND = "
                        + user.getEmail());

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}