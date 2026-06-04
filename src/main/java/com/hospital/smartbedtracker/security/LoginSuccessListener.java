package com.hospital.smartbedtracker.security;

import com.hospital.smartbedtracker.entity.LoginAudit;
import com.hospital.smartbedtracker.repository.LoginAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginSuccessListener {

    private final LoginAuditRepository repository;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {

        String username =
                event.getAuthentication().getName();

        String role =
                event.getAuthentication()
                        .getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority();

        LoginAudit audit = LoginAudit.builder()
                .username(username)
                .role(role)
                .action("LOGIN")
                .actionTime(LocalDateTime.now())
                .build();

        repository.save(audit);

        System.out.println(
                "LOGIN AUDIT SAVED : "
                        + username
        );
    }
}