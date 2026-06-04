package com.hospital.smartbedtracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.smartbedtracker.enums.BedStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "beds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bed_seq")
    @SequenceGenerator(name = "bed_seq", sequenceName = "bed_sequence", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Bed number is required")
    @Column(nullable = false, unique = true)
    private String bedNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BedStatus status = BedStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ward_id")
    @JsonIgnoreProperties("beds")
    private Ward ward;

    @OneToOne(mappedBy = "bed", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"bed", "hibernateLazyInitializer"})
    private Patient currentPatient;

    // ✅ PostgreSQL stores LocalDateTime natively as TIMESTAMP
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    private void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
