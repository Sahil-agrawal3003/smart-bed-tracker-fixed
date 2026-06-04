package com.hospital.smartbedtracker;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String[] users = {
                "123456",      // Sahil
                "rahul123",    // Rahul
                "aman123",     // Aman
                "rohit123",    // Rohit
                "vishal123",   // Vishal
                "neha123",     // Neha
                "priya123",    // Priya
                "ankit123",    // Ankit
                "sonu123",     // Sonu
                "admin123"     // Admin
        };

        System.out.println(
                "=========== GENERATED HASHES ===========");

        for (String password : users) {

            String hash =
                    encoder.encode(password);

            System.out.println(
                    "Password : " + password);

            System.out.println(
                    "Hash     : " + hash);

            System.out.println(
                    "--------------------------------");
        }
    }
}