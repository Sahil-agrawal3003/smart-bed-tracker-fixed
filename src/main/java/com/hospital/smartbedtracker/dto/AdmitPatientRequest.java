package com.hospital.smartbedtracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdmitPatientRequest {

    @NotBlank(message = "Patient name is required")
    private String name;

    @Min(value = 0, message = "Age must be non-negative")
    private int age;

    private String disease;

    private String doctorAssigned;

    private String contactNumber;

    private String emergencyContact;
}
