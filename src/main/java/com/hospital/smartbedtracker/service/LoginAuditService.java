package com.hospital.smartbedtracker.service;

import com.hospital.smartbedtracker.entity.LoginAudit;
import com.hospital.smartbedtracker.repository.LoginAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginAuditService {

    private final LoginAuditRepository repository;

    public void saveLogin(String username, String role) {

        LoginAudit audit = LoginAudit.builder()
                .username(username)
                .role(role)
                .action("LOGIN")
                .actionTime(LocalDateTime.now())
                .build();

        repository.save(audit);
    }

    public void saveLogout(String username, String role) {

        LoginAudit audit = LoginAudit.builder()
                .username(username)
                .role(role)
                .action("LOGOUT")
                .actionTime(LocalDateTime.now())
                .build();

        repository.save(audit);
    }
}