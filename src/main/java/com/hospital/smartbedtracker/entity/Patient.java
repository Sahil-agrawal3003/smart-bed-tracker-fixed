package com.hospital.smartbedtracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.smartbedtracker.enums.PatientStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patient_seq")
    @SequenceGenerator(name = "patient_seq", sequenceName = "patient_sequence", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Patient name is required")
    @Column(nullable = false)
    private String name;

    @Min(value = 0, message = "Age must be non-negative")
    private int age;

    // ✅ 'disease' is a reserved word in some PG versions — map to a safe column name
    @Column(name = "diagnosis")
    private String disease;

    @Column(name = "doctor_assigned")
    private String doctorAssigned;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bed_id")
    @JsonIgnoreProperties({"currentPatient", "hibernateLazyInitializer"})
    private Bed bed;

    @Column(name = "admission_date")
    private LocalDateTime admissionDate;

    @Column(name = "discharge_date")
    private LocalDateTime dischargeDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PatientStatus status = PatientStatus.ADMITTED;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;
}
