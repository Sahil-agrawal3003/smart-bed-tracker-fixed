package com.hospital.smartbedtracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ward {

    @Id
    // ✅ PostgreSQL: use SEQUENCE strategy (IDENTITY also works in PG 10+ but SEQUENCE is canonical)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ward_seq")
    @SequenceGenerator(name = "ward_seq", sequenceName = "ward_sequence", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Ward name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @Positive(message = "Total beds must be positive")
    private int totalBeds;

    @NotBlank(message = "Floor is required")
    private String floor;

    private String description;
}
