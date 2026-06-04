package com.hospital.smartbedtracker.controller;

import com.hospital.smartbedtracker.dto.RegisterRequest;
import com.hospital.smartbedtracker.entity.AppUser;
import com.hospital.smartbedtracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public AuthController(
            UserRepository repository,
            PasswordEncoder encoder) {

        this.repository = repository;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if(repository.findByEmail(
                request.getEmail()).isPresent()) {

            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        AppUser user = new AppUser();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                encoder.encode(
                        request.getPassword()));

        user.setRole("USER");

        repository.save(user);

        return ResponseEntity.ok(
                "Registration successful");
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String,String>> me(
            Authentication authentication) {

        String username =
                authentication.getName();

        String role =
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(a ->
                                a.equals("ROLE_ADMIN"))
                        .findFirst()
                        .orElse("ROLE_USER")
                        .replace("ROLE_", "");

        return ResponseEntity.ok(
                Map.of(
                        "username", username,
                        "role", role
                )
        );
    }
}