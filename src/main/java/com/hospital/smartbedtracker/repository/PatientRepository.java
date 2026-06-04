package com.hospital.smartbedtracker.repository;

import com.hospital.smartbedtracker.entity.Patient;
import com.hospital.smartbedtracker.enums.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByStatus(PatientStatus status);
    Optional<Patient> findByBedId(Long bedId);
    List<Patient> findByDoctorAssigned(String doctor);

    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.bed WHERE p.status = 'ADMITTED'")
    List<Patient> findAdmittedWithBed();
}
