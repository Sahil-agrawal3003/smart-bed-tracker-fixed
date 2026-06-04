package com.hospital.smartbedtracker.controller;

import com.hospital.smartbedtracker.dto.AdmitPatientRequest;
import com.hospital.smartbedtracker.entity.Patient;
import com.hospital.smartbedtracker.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    /** GET /api/patients — list all patients */
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    /** GET /api/patients/{id} — single patient */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    /** GET /api/patients/admitted — currently admitted patients */
    @GetMapping("/admitted")
    public ResponseEntity<List<Patient>> getAdmittedPatients() {
        return ResponseEntity.ok(patientService.getAdmittedPatients());
    }

    /** POST /api/patients/admit/{bedId} — admit a patient to a bed */
    @PostMapping("/admit/{bedId}")
    public ResponseEntity<Patient> admitPatient(
            @Valid @RequestBody AdmitPatientRequest request,
            @PathVariable Long bedId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.admitPatient(request, bedId));
    }

    /** PUT /api/patients/{id}/discharge — discharge a patient */
    @PutMapping("/{id}/discharge")
    public ResponseEntity<Patient> dischargePatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.dischargePatient(id));
    }

    /** PUT /api/patients/{id}/transfer/{newBedId} — transfer a patient */
    @PutMapping("/{id}/transfer/{newBedId}")
    public ResponseEntity<Patient> transferPatient(
            @PathVariable Long id,
            @PathVariable Long newBedId) {
        return ResponseEntity.ok(patientService.transferPatient(id, newBedId));
    }
}
